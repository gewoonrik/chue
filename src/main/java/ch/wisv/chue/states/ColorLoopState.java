package ch.wisv.chue.states;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

/**
 * @author Sander Ploegsma
 */
public class ColorLoopState implements IHueState {
    @Override
    public void execute(PHBridge bridge, String... lightIdentifiers) {
        for (String lightId : lightIdentifiers) {
            PHLightState lightState = new PHLightState();
            lightState.setEffectMode(PHLight.PHLightEffectMode.EFFECT_COLORLOOP);
            bridge.updateLightState(lightId, lightState, null); // If no bridge response is required then use this simpler form.
        }
    }
}
