package nl.inholland.oop1.model;

/**
 * A light in the smart home.
 */
public class LightDevice extends Device {

    /**
     * Creates a light without an id.
     *
     * @param name       the name of the light; must not be {@code null} or blank
     * @param powerUsage the power usage in watts; must be a finite number of at least 0
     * @throws IllegalArgumentException if the name or the power usage is invalid
     */
    public LightDevice(String name, double powerUsage) {
        super(name, powerUsage);
    }

    /**
     * Returns the type of this device.
     *
     * @return always {@code "Light"}
     */
    @Override
    public String getType() {
        return "Light";
    }
}
