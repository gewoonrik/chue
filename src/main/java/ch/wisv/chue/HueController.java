package ch.wisv.chue;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.List;
import java.util.Random;

@Controller
public class HueController {
    private static final int MAX_HUE = 65535;
    private static final Logger log = LoggerFactory.getLogger(HueController.class);

    @Value("${BridgeUsername}")
    private String username;
    @Value("${BridgeHostname}")
    private String hostname;
    
    private PHHueSDK phHueSDK = PHHueSDK.getInstance();
    private PHBridge bridge;
    private PHBridgeResourcesCache cache;

    /**
     * Connect to the last known access point.
     * This method is triggered by the Connect to Bridge button but it can equally be used to automatically connect 
     * * to a bridge.
     */
    @PostConstruct
    public void connectToBridge() {
        log.info("Connecting");
        if (username == null || hostname == null) {
            throw new RuntimeException("Missing hostname or username.");
        }
        phHueSDK.getNotificationManager().registerSDKListener(listener);
        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setUsername(username);
        accessPoint.setIpAddress(hostname);
        phHueSDK.connect(accessPoint);
    }

    private PHSDKListener listener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPointsList) {
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
        }

        @Override
        public void onBridgeConnected(PHBridge bridge) {
            phHueSDK.setSelectedBridge(bridge);
            phHueSDK.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
            HueController.this.bridge = bridge;
            HueController.this.cache = bridge.getResourceCache();
            log.info("Connected");
        }

        @Override
        public void onCacheUpdated(List<Integer> arg0, PHBridge arg1) {
        }

        @Override
        public void onConnectionLost(PHAccessPoint arg0) {
        }

        @Override
        public void onConnectionResumed(PHBridge arg0) {
        }

        @Override
        public void onError(int code, final String message) {
            if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
                log.error("Not responding");
            } else if (code == PHMessageType.PUSHLINK_BUTTON_NOT_PRESSED) {
                log.error("Pushlink button not pressed");
            } else if (code == PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
                log.error("Authentication failed");
            } else if (code == PHMessageType.BRIDGE_NOT_FOUND) {
                log.error("Not found");
            }
            log.error("\tMessage: " + message);
        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> parsingErrorsList) {
            for (PHHueParsingError parsingError : parsingErrorsList) {
                System.out.println("ParsingError : " + parsingError.getMessage());
            }
        }
    };

    public void randomLights() {
        List<PHLight> allLights = cache.getAllLights();
        Random rand = new Random();

        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setEffectMode(PHLight.PHLightEffectMode.EFFECT_NONE);
            lightState.setHue(rand.nextInt(MAX_HUE));
            bridge.updateLightState(light, lightState); // If no bridge response is required then use this simpler form.
        }
    }

    public void colorLoop() {
        List<PHLight> allLights = cache.getAllLights();
        Random rand = new Random();

        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            lightState.setEffectMode(PHLight.PHLightEffectMode.EFFECT_COLORLOOP);
            bridge.updateLightState(light, lightState); // If no bridge response is required then use this simpler form.
        }
    }

    public void changeLights(String lightIdentifer, String rgb) {
        Color color = Color.decode(rgb);
        changeLights(lightIdentifer, color);
    }

    public void changeLights(String lightIdentifer, Color color) {
        if ("all".equals(lightIdentifer)) {
            List<PHLight> allLights = cache.getAllLights();
            for (PHLight light : allLights) {
                changeLights(light.getIdentifier(), color);
            }
        }
        else {
            PHLightState lightState = new PHLightState();
            float xy[] = PHUtilities.calculateXYFromRGB(color.getRed(), color.getGreen(), color.getBlue(), "LCT001");
            lightState.setEffectMode(PHLight.PHLightEffectMode.EFFECT_NONE);
            lightState.setX(xy[0]);
            lightState.setY(xy[1]);

            phHueSDK.getSelectedBridge().updateLightState(lightIdentifer, lightState, null);
        }
    }
}
