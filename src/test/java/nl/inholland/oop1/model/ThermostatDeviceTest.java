package nl.inholland.oop1.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ThermostatDeviceTest {

    @Test
    void getTypeReturnsThermostat() {
        assertEquals("Thermostat", new ThermostatDevice("Hall", 5, "16-24").getType());
    }

    @Test
    void isADevice() {
        assertInstanceOf(Device.class, new ThermostatDevice("Hall", 5, "16-24"));
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            "16-24;       16;   24",
            "16.5-23.5;   16.5; 23.5",
            "5-30;        5;    30",      // both bounds inclusive
            "' 16 - 24 '; 16;   24",      // whitespace allowed (D-10)
            "29.9-30;     29.9; 30"
    })
    void constructorParsesValidRange(String range, double expectedMin, double expectedMax) {
        ThermostatDevice thermostat = new ThermostatDevice("Hall", 5, range);

        assertEquals(expectedMin, thermostat.getMinimumTemperature());
        assertEquals(expectedMax, thermostat.getMaximumTemperature());
    }

    @Test
    void newThermostatHasMinimumAsTarget() {
        assertEquals(16.5, new ThermostatDevice("Hall", 5, "16.5-23.5").getTargetTemperature());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "   ",
            "16",           // no maximum
            "16-",          // missing maximum
            "-24",          // missing minimum
            "16-20-24",     // too many parts
            "abc-24",       // not a number
            "16,5-23,5",    // comma decimal separator (D-10)
            "4.9-20",       // minimum below 5
            "4-20",
            "16-30.1",      // maximum above 30
            "16-31",
            "24-16",        // min greater than max
            "20-20",        // min equal to max
            "NaN-20",
            "16-Infinity"
    })
    void constructorRejectsInvalidRange(String range) {
        assertThrows(IllegalArgumentException.class, () -> new ThermostatDevice("Hall", 5, range));
    }

    @Test
    void constructorValidatesNameAndPowerUsage() {
        assertThrows(IllegalArgumentException.class, () -> new ThermostatDevice(null, 5, "16-24"));
        assertThrows(IllegalArgumentException.class, () -> new ThermostatDevice("Hall", -5, "16-24"));
    }

    @ParameterizedTest
    @ValueSource(doubles = {16, 20.5, 24})   // bounds are inclusive (D-11)
    void setTargetTemperatureAcceptsValueInRange(double target) {
        ThermostatDevice thermostat = new ThermostatDevice("Hall", 5, "16-24");

        thermostat.setTargetTemperature(target);

        assertEquals(target, thermostat.getTargetTemperature());
    }

    @ParameterizedTest
    @ValueSource(doubles = {15.9, 24.1, 0, -10, Double.NaN})
    void setTargetTemperatureRejectsValueOutsideRangeAndKeepsOldTarget(double target) {
        ThermostatDevice thermostat = new ThermostatDevice("Hall", 5, "16-24");
        thermostat.setTargetTemperature(20);

        assertThrows(IllegalArgumentException.class, () -> thermostat.setTargetTemperature(target));
        assertEquals(20, thermostat.getTargetTemperature());
    }

    @Test
    void describeAddsRangeAndTarget() {
        ThermostatDevice thermostat = new ThermostatDevice("Hall", 5, "16-24");
        thermostat.setId(2);
        thermostat.setTargetTemperature(21.5);

        assertEquals("| id: 2 || name: Hall || type: Thermostat || range: 16.0-24.0 || target: 21.5 |",
                thermostat.describe());
    }

    @Test
    void describeWithoutIdShowsNull() {
        assertEquals("| id: null || name: Hall || type: Thermostat || range: 16.0-24.0 || target: 16.0 |",
                new ThermostatDevice("Hall", 5, "16-24").describe());
    }

    @Test
    void toStringReturnsDescribeThroughDeviceReference() {
        Device device = new ThermostatDevice("Hall", 5, "16-24");

        assertEquals(device.describe(), device.toString());
    }
}
