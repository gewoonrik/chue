package ch.wisv.chue;

import ch.wisv.chue.events.IHueEvent;
import ch.wisv.chue.states.BlankState;
import ch.wisv.chue.states.IHueState;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.bridge.impl.PHBridgeImpl;
import com.philips.lighting.hue.sdk.connection.impl.PHHueHttpConnection;
import com.philips.lighting.hue.sdk.connection.impl.PHLocalBridgeDelegator;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import org.json.hue.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class HueService {

    public static final int MAX_HUE = 65535;
    private static final Logger log = LoggerFactory.getLogger(HueService.class);

    public interface Command {
        void execute();
    }

    @Value("${BridgeUsername}")
    private String username;
    @Value("${BridgeHostname}")
    private String hostname;

    private PHHueSDK phHueSDK = PHHueSDK.getInstance();
    private PHBridge bridge;

    private Command restoreState;

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
            HueService.this.bridge = bridge;
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

    private String[] getLightIdentifiers(String... lightIdentifiers) {
        if ("all".equals(lightIdentifiers[0])) {
            List<PHLight> lights = getAllLights();
            String[] res = new String[lights.size()];
            for (int i = 0; i < lights.size(); i++) {
                res[i] = lights.get(i).getIdentifier();
            }
            return res;
        } else {
            return lightIdentifiers;
        }
    }

    public void loadState(IHueState state, String... lightIdentifiers) {
        String[] ids = getLightIdentifiers(lightIdentifiers);
        restoreState = () -> {
            new BlankState().execute(this.bridge, ids);
            state.execute(this.bridge, ids);
            log.debug("Light states restored!");
        };

        // Set everything to default before loading state.
        new BlankState().execute(this.bridge, ids);
        state.execute(this.bridge, ids);
    }

    public void loadEvent(IHueEvent event, int duration, String... lightIdentifiers) {
        String[] ids = getLightIdentifiers(lightIdentifiers);
        event.execute(this.bridge, ids);

        Runnable restore = () -> {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                log.warn("Interrupted, not restoring light states! Exception: " + e.getMessage());
            }
            log.debug("Restoring light states after event.");
            if (this.restoreState != null)
                this.restoreState.execute();
            else
                new BlankState().execute(this.bridge, lightIdentifiers);
        };
        new Thread(restore, "ServiceThread").start();
    }

    /**
     * Strobes for some time
     *
     * @param millis           duration
     * @param lightIdentifiers which ligts
     * @see <a href="http://www.lmeijer.nl/archives/225-Do-hue-want-a-strobe-up-there.html">Strobe with Hue by Leon Meijer</a>
     */
    public void strobe(int millis, String... lightIdentifiers) {
        PHHueHttpConnection connection = new PHHueHttpConnection();
        final String httpAddress = ((PHLocalBridgeDelegator) ((PHBridgeImpl) bridge).getBridgeDelegator()).buildHttpAddress().toString();

        // Put a light definition aka `symbol` at bulb, using internal API call
        for (String light : lightIdentifiers) {
            JSONObject pointSymbol = new JSONObject();
            pointSymbol.put("1", "0A00F1F01F1F1001F1FF100000000001F2F");
            String resp = connection.putData(pointSymbol.toString(), httpAddress + "lights/" + light + "/pointsymbol");
            log.debug(resp);
        }

        boolean allLightsTurnedOn = bridge.getResourceCache().getAllLights().stream()
                .allMatch(light -> light.getLastKnownLightState().isOn());

        if(allLightsTurnedOn) {
            // Activate symbol
            // Kinda magic symbolselection. It is something like this:
            // for 01..05 step 01, [0i0x]+ where i is `symbol` and x is light bulb
            JSONObject strobeJSON = new JSONObject();
            strobeJSON.put("symbolselection", "01010301010102010301");
            strobeJSON.put("duration", millis);
            //group 0 contains all lights
            String resp = connection.putData(strobeJSON.toString(), httpAddress + "groups/0/transmitsymbol");
            log.debug(resp);
        }
    }

    public void strobe(int millis) {
        final List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        String[] ls = new String[allLights.size()];
        for (int i = 0; i < allLights.size(); i++) {
            ls[i] = allLights.get(i).getIdentifier();
        }
        strobe(millis, ls);
    }

    public List<PHLight> getAllLights() {
        return bridge.getResourceCache().getAllLights();
    }
}
