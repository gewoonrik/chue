package ch.wisv.chue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

/**
 * Spring MVC Web Controller
 */
@Controller
public class WebController {

    @Autowired
    HueController hue;

    @RequestMapping("/")
    String index(Model model) {
        model.addAttribute("lights", hue.getLights());
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


    @RequestMapping({"/oranje", "/54"})
    @ResponseBody
    String oranje() {
        hue.changeLights("all", "#FFA723");
        return "B'voranje";
    }
    
    @RequestMapping(value = "/color/{id}/{hex}", method = RequestMethod.GET)
    @ResponseBody
    String color(@PathVariable String id, @PathVariable String hex) {
        hue.changeLights(id, '#' + hex);
        return "OK";
    }

    @RequestMapping(value = "/color", method = RequestMethod.POST)
    @ResponseBody
    String colorPost(@RequestParam(value = "id[]") String[] id, @RequestParam String hex, @RequestParam(defaultValue = "400") int transitionTime) {
        hue.changeLights(Arrays.asList(id), hex, transitionTime);
        return "OK";
    }
}
