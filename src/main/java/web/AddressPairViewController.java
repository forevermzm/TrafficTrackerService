package web;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import service.DefaultSourceDestinationService;

import static com.google.common.base.Preconditions.checkNotNull;

@Controller
@RequestMapping(value = "pairs")
@Component
public class AddressPairViewController {

    private final DefaultSourceDestinationService service;

    @Autowired
    public AddressPairViewController(DefaultSourceDestinationService service) {
        this.service = checkNotNull(service);
    }

    @GetMapping
    public ModelAndView getAll(Model model) {
        ModelAndView mav = new ModelAndView("addressPair");
        mav.addObject("pairs", service.getAll());
        return mav;
    }

    @GetMapping(path = "trafficStatus")
    public ModelAndView getTrafficStatus(@RequestParam(value="pairId") String pairId) {
        ModelAndView mav = new ModelAndView("trafficStatus");
        mav.addObject("pairId", pairId);
        mav.addObject("status", new ArrayList<>(Arrays.asList("Year", "Name", "Good")));
        return mav;
    }
}
