package nl.inholland.oop1.model;

/**
 * Base class for every device in the smart home.
 * <p>
 * A device has a name and a power usage in watts. A new device has no id ({@code null});
 * the id is assigned when the device is added to a room of a {@link SmartHome}.
 */
public abstract class Device implements Describable {

    private Integer id;
    private String name;
    private double powerUsage;

    /**
     * Creates a device without an id.
     *
     * @param name       the name of the device; must not be {@code null} or blank
     * @param powerUsage the power usage in watts; must be a finite number of at least 0
     * @throws IllegalArgumentException if the name or the power usage is invalid
     */
    public Device(String name, double powerUsage) {
        this.name = validateName(name);
        this.powerUsage = validatePowerUsage(powerUsage);
    }

    /**
     * Returns the id of this device.
     *
     * @return the id, or {@code null} if the device has not been added to a room yet
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the id of this device. Normally only called by {@link SmartHome}.
     *
     * @param id the new id, may be {@code null}
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the name of this device.
     *
     * @return the name, never {@code null} or blank
     */
    public String getName() {
        return name;
    }

    /**
     * Changes the name of this device.
     *
     * @param name the new name; must not be {@code null} or blank
     * @throws IllegalArgumentException if the name is {@code null} or blank
     */
    public void setName(String name) {
        this.name = validateName(name);
    }

    /**
     * Returns the power usage of this device.
     *
     * @return the power usage in watts, at least 0
     */
    public double getPowerUsage() {
        return powerUsage;
    }

    /**
     * Changes the power usage of this device.
     *
     * @param powerUsage the new power usage in watts; must be a finite number of at least 0
     * @throws IllegalArgumentException if the power usage is negative, {@code NaN} or infinite
     */
    public void setPowerUsage(double powerUsage) {
        this.powerUsage = validatePowerUsage(powerUsage);
    }

    /**
     * Returns the type of this device, for example {@code "Light"}.
     *
     * @return the type name, never {@code null}
     */
    public abstract String getType();

    /**
     * Returns a description in the format {@code | id: <id> || name: <name> || type: <type> |}.
     *
     * @return the description of this device
     */
    @Override
    public String describe() {
        return "| id: " + id + " || name: " + name + " || type: " + getType() + " |";
    }

    /**
     * Returns exactly the same text as {@link #describe()}.
     *
     * @return the description of this device
     */
    @Override
    public String toString() {
        return describe();
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be empty.");
        }
        return name;
    }

    private static double validatePowerUsage(double powerUsage) {
        // NaN fails every comparison, so test for the valid range instead of the invalid one.
        if (!(powerUsage >= 0) || Double.isInfinite(powerUsage)) {
            throw new IllegalArgumentException("Power usage must be a number of at least 0.");
        }
        return powerUsage;
    }
}
