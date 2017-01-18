package service;

import dao.MapsDAO;
import dao.SourceDestinationTableDAO;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pojo.dynamo.SourceDestinationPair;
import pojo.json.GoogleAddress;
import pojo.json.SrcDestPair;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static exceptions.RequestChecker.checkRequestInput;

@RestController
@RequestMapping(value = "srcDestPair")
@Component
public class DefaultSourceDestinationService {

    private SourceDestinationTableDAO ddbDao;
    private MapsDAO mapsDao;

    @Autowired
    public DefaultSourceDestinationService(SourceDestinationTableDAO ddbDao,
                                           MapsDAO mapsDao) {
        this.ddbDao = checkNotNull(ddbDao);
        this.mapsDao = checkNotNull(mapsDao);
    }

    @PostMapping
    public void addNew(@RequestParam(value="src") String src,
                       @RequestParam(value="dest") String dest) {
        checkRequestInput(Strings.isNotBlank(src), "Src address cannot be blank");
        checkRequestInput(Strings.isNotBlank(dest), "Dest address cannot be blank");

        GoogleAddress srcAddress = mapsDao.getGoogleAddress(src);
        GoogleAddress destAddress = mapsDao.getGoogleAddress(dest);

        SrcDestPair pair = SrcDestPair.builder()
                .withSrcAddress(srcAddress)
                .withDestAddress(destAddress)
                .build();

        checkRequestInput(ddbDao.read(pair.getId()) == null, "Pair already exists");

        SourceDestinationPair ddbEntry = new SourceDestinationPair();
        ddbEntry.setId(pair.getId());
        ddbEntry.setPair(pair);
        ddbEntry.setCreationDate(Instant.now());

        // Never updated.
        ddbEntry.setLastUpdateTime(Instant.EPOCH);

        ddbDao.save(ddbEntry);
    }

    @GetMapping
    public List<SrcDestPair> getAll() {
        return ddbDao.scan(100)
                .stream()
                .map(s -> s.getPair())
                .collect(Collectors.toList());
    }

}
