package ch.wisv.chue;

import ch.wisv.chue.events.HueEvent;
import ch.wisv.chue.states.HueState;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;
import java.util.Random;

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
        hue.loadEvent(new HueEvent() {
            @Override
            public void execute() {
                List<PHLight> allLights = cache.getAllLights();
                Random rand = new Random();

                for (PHLight light : allLights) {
                    int randHue = rand.nextInt(hue.MAX_HUE);

                    PHLightState lightState = new PHLightState();
                    lightState.setEffectMode(PHLight.PHLightEffectMode.EFFECT_NONE);
                    lightState.setHue(randHue);
                    bridge.updateLightState(light, lightState); // If no bridge response is required then use this simpler form.
                }
            }
        }, 1000);
        return "Randomised";
    }

    @RequestMapping("/colorloop")
    @ResponseBody
    String colorLoop() {
        hue.loadState(new HueState() {
            @Override
            public void execute() {
                List<PHLight> allLights = cache.getAllLights();

                for (PHLight light : allLights) {
                    PHLightState lightState = new PHLightState();
                    lightState.setEffectMode(PHLight.PHLightEffectMode.EFFECT_COLORLOOP);
                    bridge.updateLightState(light, lightState); // If no bridge response is required then use this simpler form.
                }
            }
        });
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
        return String.format("Alerting for %d milliseconds", timeout);
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
