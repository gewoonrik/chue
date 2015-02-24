package ch.wisv.chue;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * HueProperties.java
 * <p/>
 * Stores the last known connected IP Address and the last known username.
 * This facilitates automatic bridge connection.
 * <p/>
 * Also, as the username (for the whitelist) is a random string,  this prevents the need to
 * pushlink every time the app is started (as the username is read from the properties file).
 */
public final class HueProperties {

    private static final String USERNAME = "BridgeUsername";
    private static final String HOSTNAME = "BridgeHostname";
    private static final String PROPS_FILE_NAME = "hue.properties";
    private static Properties props = null;

    private HueProperties() {
    }

    public static String getUsername() {
        return props.getProperty(USERNAME);
    }

    public static String getHostname() {
        return props.getProperty(HOSTNAME);
    }

    public static void loadProperties() {
        if (props == null) {
            props = new Properties();
            FileInputStream in;

            try {
                in = new FileInputStream(PROPS_FILE_NAME);
                props.load(in);
                in.close();
            } catch (IOException e) {
                // Handle the IOException.
            }
        }
    }

}