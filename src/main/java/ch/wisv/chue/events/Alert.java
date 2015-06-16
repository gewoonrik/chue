package ch.wisv.chue.events;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

/**
 * @author Sander Ploegsma
 */
public class Alert implements IHueEvent {

    public Alert() {

    }

    public void execute(PHBridge bridge, String... lightIdentifiers) {

        for (String id : lightIdentifiers) {
            PHLightState lightState = new PHLightState();
            lightState.setTransitionTime(0);
            lightState.setAlertMode(PHLight.PHLightAlertMode.ALERT_LSELECT);
            bridge.updateLightState(id, lightState, null);
        }
    }
}
