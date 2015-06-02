package ch.wisv.chue;

import ch.wisv.chue.events.Alert;
import ch.wisv.chue.states.ColorLoopState;
import ch.wisv.chue.states.ColorState;
import ch.wisv.chue.states.RandomColorLoopState;
import ch.wisv.chue.states.RandomColorState;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

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

    @RequestMapping("/random/{id}")
    @ResponseBody
    String random(@PathVariable String id) {
        hue.loadState(new RandomColorState(), id);
        return "Randomised";
    }

    @RequestMapping("/strobe/all")
    @ResponseBody
    String strobeAll(@RequestParam(value = "duration", defaultValue = "500") Integer duration) {
        hue.strobe(duration);
        return "Strobe for duration=" + duration + "ms";
    }

    @RequestMapping("/strobe")
    @ResponseBody
    String strobe(@RequestParam(value = "id[]") String[] id, @RequestParam(value = "duration", defaultValue = "500") Integer duration) {
        hue.strobe(duration, id);
        return "Strobe lamps (" + Arrays.asList(id) + ") for duration=" + duration + "ms";
    }

    @RequestMapping("/colorloop/{id}")
    @ResponseBody
    String colorLoop(@PathVariable String id) {
        hue.loadState(new ColorLoopState(), id);
        return "Colorloop";
    }

    @RequestMapping("/randomcolorloop/{id}")
    @ResponseBody
    String randomColorLoop(@PathVariable String id) {
        hue.loadState(new RandomColorLoopState(), id);
        return "Random Colorloop";
    }

    @RequestMapping("/alert/{id}")
    @ResponseBody
    String alert(@RequestParam(value = "timeout", defaultValue = "5000") Integer timeout, @PathVariable String id) {
        hue.loadEvent(new Alert(), timeout, id);
        return String.format("Alerting for %d milliseconds", timeout);
    }


    @RequestMapping({"/oranje", "/54"})
    @ResponseBody
    String oranje() {
        hue.loadState(new ColorState(Color.web("#FFA723")));
        return "B'voranje";
    }

    @RequestMapping(value = "/color/{id}/{hex:[a-fA-F0-9]{6}}", method = RequestMethod.GET)
    @ResponseBody
    String color(@PathVariable String id, @PathVariable String hex) {
        Color color = Color.web('#' + hex);

        hue.loadState(new ColorState(color), id);

        return "Changed colour of lamps (" + id + ") to #" + hex;
    }

    @RequestMapping(value = "/color/{id}/{colorName:(?![a-fA-F0-9]{6}).*}", method = RequestMethod.GET)
    @ResponseBody
    String colorFriendly(@PathVariable String id, @PathVariable String colorName) {
        Color color = Color.valueOf(colorName);

        hue.loadState(new ColorState(color), id);

        String hex = String.format("%02x%02x%02x",
                (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));

        return "Changed colour of lamps (" + id + ") to #" + hex;
    }
}
