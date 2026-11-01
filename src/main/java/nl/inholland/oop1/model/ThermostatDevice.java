package nl.inholland.oop1.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A thermostat with a fixed temperature range and an adjustable target temperature.
 */
public class ThermostatDevice extends Device {

    private static final double LOWEST_ALLOWED = 5.0;
    private static final double HIGHEST_ALLOWED = 30.0;
    // "<min>-<max>", decimal point optional, whitespace around the numbers allowed.
    private static final Pattern RANGE_PATTERN =
            Pattern.compile("\\s*(\\d+(?:\\.\\d+)?)\\s*-\\s*(\\d+(?:\\.\\d+)?)\\s*");

    private final double minimumTemperature;
    private final double maximumTemperature;
    private double targetTemperature;

    /**
     * Creates a thermostat without an id. The target temperature starts at the minimum of the range.
     *
     * @param name             the name of the thermostat; must not be {@code null} or blank
     * @param powerUsage       the power usage in watts; must be a finite number of at least 0
     * @param temperatureRange the range in the format {@code "<min>-<max>"}, for example
     *                         {@code "16-24"} or {@code "16.5-23.5"}; both values must lie within
     *                         5 and 30 °C (inclusive) and min must be lower than max
     * @throws IllegalArgumentException if the name, power usage or temperature range is invalid
     */
    public ThermostatDevice(String name, double powerUsage, String temperatureRange) {
        super(name, powerUsage);
        if (temperatureRange == null) {
            throw new IllegalArgumentException("Temperature range must not be empty.");
        }
        Matcher matcher = RANGE_PATTERN.matcher(temperatureRange);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "Temperature range '" + temperatureRange + "' must have the format <min>-<max>.");
        }
        double min = Double.parseDouble(matcher.group(1));
        double max = Double.parseDouble(matcher.group(2));
        if (min < LOWEST_ALLOWED || max > HIGHEST_ALLOWED) {
            throw new IllegalArgumentException("Temperature range must lie within 5 and 30.");
        }
        if (min >= max) {
            throw new IllegalArgumentException("Minimum temperature must be lower than maximum temperature.");
        }
        this.minimumTemperature = min;
        this.maximumTemperature = max;
        this.targetTemperature = min;
    }

    /**
     * Returns the lowest temperature this thermostat can be set to.
     *
     * @return the minimum temperature in °C
     */
    public double getMinimumTemperature() {
        return minimumTemperature;
    }

    /**
     * Returns the highest temperature this thermostat can be set to.
     *
     * @return the maximum temperature in °C
     */
    public double getMaximumTemperature() {
        return maximumTemperature;
    }

    /**
     * Returns the current target temperature.
     *
     * @return the target temperature in °C
     */
    public double getTargetTemperature() {
        return targetTemperature;
    }

    /**
     * Changes the target temperature.
     *
     * @param targetTemperature the new target in °C; must lie within the range (inclusive)
     * @throws IllegalArgumentException if the target lies outside the range or is {@code NaN}
     */
    public void setTargetTemperature(double targetTemperature) {
        if (!(targetTemperature >= minimumTemperature && targetTemperature <= maximumTemperature)) {
            throw new IllegalArgumentException("Target temperature " + targetTemperature
                    + " is outside the range " + minimumTemperature + "-" + maximumTemperature + ".");
        }
        this.targetTemperature = targetTemperature;
    }

    /**
     * Returns the type of this device.
     *
     * @return always {@code "Thermostat"}
     */
    @Override
    public String getType() {
        return "Thermostat";
    }

    /**
     * Returns the device description extended with the range and the target temperature, e.g.
     * {@code | id: 1 || name: Hall || type: Thermostat || range: 16.0-24.0 || target: 16.0 |}.
     *
     * @return the description of this thermostat
     */
    @Override
    public String describe() {
        String base = super.describe();
        // Drop the closing "|" of the base description and continue in the same style.
        return base.substring(0, base.length() - 1)
                + "|| range: " + minimumTemperature + "-" + maximumTemperature
                + " || target: " + targetTemperature + " |";
    }
}
