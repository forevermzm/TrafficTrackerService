package web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import pojo.json.TrafficStatusData;
import service.DefaultSourceDestinationService;
import service.DefaultTrafficStatusService;

import static com.google.common.base.Preconditions.checkNotNull;

@Controller
@RequestMapping(value = "pairs")
@Component
public class AddressPairViewController {

    private final DefaultSourceDestinationService sourceDestinationService;
    private final DefaultTrafficStatusService trafficStatusService;

    @Autowired
    public AddressPairViewController(DefaultSourceDestinationService sourceDestinationService,
                                     DefaultTrafficStatusService trafficStatusService) {
        this.sourceDestinationService = checkNotNull(sourceDestinationService);
        this.trafficStatusService = checkNotNull(trafficStatusService);
    }

    @GetMapping
    public ModelAndView getAll(Model model) {
        ModelAndView mav = new ModelAndView("addressPair");
        mav.addObject("pairs", sourceDestinationService.getAll());
        return mav;
    }

    @GetMapping(path = "trafficStatus")
    public ModelAndView getTrafficStatus(@RequestParam(value="pairId") String pairId) {
        ModelAndView mav = new ModelAndView("trafficStatus");
        mav.addObject("pairId", pairId);
        mav.addObject("status", trafficStatusService.getTrafficStatusData(pairId));
        return mav;
    }
}
