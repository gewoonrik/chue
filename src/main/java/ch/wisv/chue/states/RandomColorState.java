package ch.wisv.chue.states;

import ch.wisv.chue.HueService;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLightState;

import java.util.Random;

/**
 * @author Sander Ploegsma
 */
public class RandomColorState implements IHueState {
    @Override
    public void execute(PHBridge bridge, String... lightIdentifiers) {
        Random rand = new Random();

        for (String id : lightIdentifiers) {
            int randHue = rand.nextInt(HueService.MAX_HUE);

            PHLightState lightState = new PHLightState();
            lightState.setHue(randHue);
            bridge.updateLightState(id, lightState, null); // If no bridge response is required then use this simpler form.
        }
    }
}
