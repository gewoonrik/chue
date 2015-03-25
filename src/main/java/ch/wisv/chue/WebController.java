package ch.wisv.chue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.awt.*;

/**
 * Spring MVC Web Controller
 */
@Controller
public class WebController {

    @Autowired
    HueController hue;

    @RequestMapping("/")
    String index(Model model) {
        model.addAttribute("lights", hue.getAllLights());
        return "index";
    }

    @RequestMapping("/random")
    @ResponseBody
    String random() {
        hue.randomLights();
        return "Randomised";
    }

    @RequestMapping("/colorloop")
    @ResponseBody
    String colorLoop() {
        hue.colorLoop();
        return "Colorloop";
    }

    @RequestMapping("/randomcolorloop")
    @ResponseBody
    String randomColorLoop() {
        hue.randomLights();
        hue.colorLoop();
        return "Random Colorloop";
    }

    @RequestMapping("/alert")
    @ResponseBody
    String alert(@RequestParam(value="timeout", defaultValue = "5000") Integer timeout) {
        hue.alert(timeout);
        return String.format("Alerting for %d seconds", timeout);
    }


    @RequestMapping({"/oranje", "/54"})
    @ResponseBody
    String oranje() {
        hue.changeLights(Color.decode("#FFA723"));
        return "B'voranje";
    }
    
    @RequestMapping(value = "/color/{id}/{hex}", method = RequestMethod.GET)
    @ResponseBody
    String color(@PathVariable String id, @PathVariable String hex) {
        if("all".equals(id)) {
            hue.changeLights(Color.decode('#' + hex));
        } else {
            hue.changeLights(Color.decode('#' + hex), id);
        }
        return "OK";
    }

    @RequestMapping(value = "/color", method = RequestMethod.POST)
    @ResponseBody
    String colorPost(@RequestParam(value = "id[]") String[] id, @RequestParam String hex, @RequestParam(defaultValue = "400") int transitionTime) {
        hue.changeLights(Color.decode(hex), transitionTime, id);
        return "OK";
    }
}
