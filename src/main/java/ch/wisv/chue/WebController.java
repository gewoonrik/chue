package ch.wisv.chue;

import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Spring MVC Web Controller
 */
@Controller
public class WebController {

    @Autowired
    HueService hue;

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
    String alert(@RequestParam(value = "timeout", defaultValue = "5000") Integer timeout) {
        hue.alert(timeout);
        return String.format("Alerting for %d milliseconds", timeout);
    }


    @RequestMapping({"/oranje", "/54"})
    @ResponseBody
    String oranje() {
        hue.changeLights(Color.web("#FFA723"));
        return "B'voranje";
    }

    @RequestMapping(value = "/color/{id}/{hex:[a-fA-F0-9]{6}}", method = RequestMethod.GET)
    @ResponseBody
    String color(@PathVariable String id, @PathVariable String hex) {
        if ("all".equals(id)) {
            hue.changeLights(Color.web('#' + hex));
        } else {
            hue.changeLights(Color.web('#' + hex), id);
        }
        return "OK";
    }

    @RequestMapping(value = "/color/{id}/{colorName:(?![a-fA-F0-9]{6}).*}", method = RequestMethod.GET)
    @ResponseBody
    String colorFriendly(@PathVariable String id, @PathVariable String colorName) {
        if ("all".equals(id)) {
            hue.changeLights(Color.valueOf(colorName));
        } else {
            hue.changeLights(Color.valueOf(colorName), id);
        }
        return "OK";
    }

    @RequestMapping(value = "/color", method = RequestMethod.POST)
    @ResponseBody
    String colorPost(@RequestParam(value = "id[]") String[] id, @RequestParam String hex, @RequestParam(defaultValue = "400") int transitionTime) {
        hue.changeLights(Color.web(hex), transitionTime, id);
        return "OK";
    }
}
