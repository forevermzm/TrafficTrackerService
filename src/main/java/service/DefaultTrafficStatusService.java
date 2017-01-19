package service;

import dao.TrafficStatusDAO;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pojo.json.GoogleTravelMode;
import pojo.json.ImmutableTrafficStatusData;
import pojo.json.TrafficStatusData;
import pojo.json.TrafficStatusDocument;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;
import static exceptions.RequestChecker.checkRequestInput;

@RestController
@RequestMapping(value = "trafficStatus")
@Component
public class DefaultTrafficStatusService {

    private static final int HOUR = 24;
    private static final int MINUTE = 60;
    private static final int SECOND = 60;
    private static final int HOUR_SLOT = 10;

    private final TrafficStatusDAO dao;

    @Autowired
    public DefaultTrafficStatusService(TrafficStatusDAO dao) {
        this.dao = checkNotNull(dao);
    }

    @GetMapping
    public TrafficStatusData getTrafficStatusData(@RequestParam(value="path") String path) {
        checkRequestInput(Strings.isNotBlank(path), "Path cannot be empty.");

        TrafficStatusDocument document = dao.read(path.replace('-', '/'));

        return convertData(document);
    }

    private TrafficStatusData convertData(TrafficStatusDocument document) {
        ImmutableTrafficStatusData.Builder dataBuilder = TrafficStatusData.builder();
        dataBuilder.withSrc(document.getSrcAddress());
        dataBuilder.withDest(document.getDestAddress());

        List<SummaryStatistics> calculator = new ArrayList<>();
        for (int i = 0; i < HOUR_SLOT * HOUR; i ++) {
            calculator.add(new SummaryStatistics());
        }

        document.getTrafficStatuses()
                .get(GoogleTravelMode.DRIVING)
                .stream()
                .filter(p -> isWeekday(p.getTime()))
                .forEach(p -> putData(calculator, p));

        List<SummaryStatistics> reverseCalculator = new ArrayList<>();
        for (int i = 0; i < HOUR_SLOT * HOUR; i ++) {
            reverseCalculator.add(new SummaryStatistics());
        }

        document.getReversedTrafficStatuses()
                .get(GoogleTravelMode.DRIVING)
                .stream()
                .filter(p -> isWeekday(p.getTime()))
                .forEach(p -> putData(reverseCalculator, p));

        for (int i = 0; i < HOUR_SLOT * HOUR; i ++) {
            dataBuilder.addData(TrafficStatusData.Data.builder()
                    .withTime(1.0 * i / HOUR_SLOT)
                    .withDuration(Objects.equals(Double.NaN, calculator.get(i).getMean()) ?
                            0.0 : calculator.get(i).getMean())
                    .withReverseDuration(Objects.equals(Double.NaN, reverseCalculator.get(i).getMean()) ?
                            0.0 : reverseCalculator.get(i).getMean())
                    .build());
        }

        return dataBuilder.build();
    }

    private boolean isWeekday(Instant instant){
        LocalDateTime localTime = LocalDateTime.ofInstant(instant, ZoneId.of("America/Los_Angeles"));
        return (localTime.getDayOfWeek() != DayOfWeek.SATURDAY &&
                localTime.getDayOfWeek() != DayOfWeek.SUNDAY);
    }

    private void putData(List<SummaryStatistics> calculator, TrafficStatusDocument.TimeDurationPair pair) {
        LocalDateTime localTime = LocalDateTime.ofInstant(pair.getTime(), ZoneId.of("America/Los_Angeles"));

        int index = localTime.getHour() * HOUR_SLOT + localTime.getMinute() / (MINUTE / HOUR_SLOT);

        calculator.get(index).addValue(pair.getDuration().getSeconds() * 1.0 / SECOND);
    }
}
