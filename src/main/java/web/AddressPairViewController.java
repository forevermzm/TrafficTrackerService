package web;

import google.GeoContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import service.DefaultSourceDestinationService;
import service.DefaultTrafficStatusService;

import static com.google.common.base.Preconditions.checkNotNull;

@Controller
@RequestMapping(value = "pairs")
@Component
public class AddressPairViewController {

    private final DefaultSourceDestinationService sourceDestinationService;
    private final DefaultTrafficStatusService trafficStatusService;

    private final String apiKey;

    @Autowired
    public AddressPairViewController(DefaultSourceDestinationService sourceDestinationService,
                                     DefaultTrafficStatusService trafficStatusService,
                                     @Qualifier("GoogleApiKey") String googleApiKey) {
        this.sourceDestinationService = checkNotNull(sourceDestinationService);
        this.trafficStatusService = checkNotNull(trafficStatusService);
        this.apiKey = googleApiKey;
    }

    @GetMapping
    public ModelAndView getAll(Model model) {
        ModelAndView mav = new ModelAndView("addressPair");
        mav.addObject("pairs", sourceDestinationService.getAll());
        return mav;
    }

    @GetMapping(path = "trafficStatus")
    public ModelAndView getTrafficStatus(@RequestParam(value="path") String path) {
        ModelAndView mav = new ModelAndView("trafficStatus");
        mav.addObject("path", path);
        mav.addObject("status", trafficStatusService.getTrafficStatusData(path));
        mav.addObject("apiKey", apiKey);
        return mav;
    }
}
