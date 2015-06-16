package ch.wisv.chue;

import com.philips.lighting.model.PHBridge;

/**
 * @author Sander Ploegsma
 */
public interface IHueCommand {

    public abstract void execute(PHBridge bridge, String... lightIdentifiers);
}
