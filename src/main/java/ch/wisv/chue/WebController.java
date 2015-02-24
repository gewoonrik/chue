package ch.wisv.chue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Spring MVC Web Controller
 */
@Controller
public class WebController {

    @Autowired
    HueController hue;

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }

    @RequestMapping("/random")
    @ResponseBody
    String random() {
        hue.randomLights();
        return "Randomised";
    }

    @RequestMapping("/oranje")
    @ResponseBody
    String oranje() {
        hue.changeLights("1", "#FFA723");
        hue.changeLights("2", "#FFA723");
        hue.changeLights("3", "#FFA723");
        return "B'voranje";
    }
}
