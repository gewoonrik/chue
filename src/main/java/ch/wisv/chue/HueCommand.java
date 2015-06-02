package ch.wisv.chue;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResourcesCache;

/**
 * @author Sander Ploegsma
 */
public abstract class HueCommand {
    protected PHBridge bridge;
    protected PHBridgeResourcesCache cache;

    public HueCommand() {

    }

    public abstract void execute();

    public void setBridge(PHBridge bridge) {
        this.bridge = bridge;
    }

    public void setCache(PHBridgeResourcesCache cache) {
        this.cache = cache;
    }
}
