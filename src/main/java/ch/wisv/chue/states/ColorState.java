package ch.wisv.chue.states;

import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLightState;
import javafx.scene.paint.Color;

/**
 * @author Sander Ploegsma
 */
public class ColorState implements IHueState {

    private Color color;

    public ColorState(Color color) {
        this.color = color;
    }

    @Override
    public void execute(PHBridge bridge, String... lightIdentifiers) {
        for (String id : lightIdentifiers) {
            PHLightState lightState = new PHLightState();
            float xy[] = PHUtilities.calculateXYFromRGB(
                    +(int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255), "LCT001");
            lightState.setX(xy[0]);
            lightState.setY(xy[1]);
            bridge.updateLightState(id, lightState, null); // If no bridge response is required then use this simpler form.
        }
    }
}
