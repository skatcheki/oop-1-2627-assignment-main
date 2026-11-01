package nl.inholland.oop1.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests the shared behaviour of {@link Device} through a minimal concrete subclass.
 */
class DeviceTest {

    /** Minimal subclass so the abstract base class can be tested on its own. */
    private static class TestDevice extends Device {
        TestDevice(String name, double powerUsage) {
            super(name, powerUsage);
        }

        @Override
        public String getType() {
            return "Test";
        }
    }

    @Test
    void constructorStoresNameAndPowerUsage() {
        Device device = new TestDevice("Lamp", 60);

        assertEquals("Lamp", device.getName());
        assertEquals(60, device.getPowerUsage());
    }

    @Test
    void newDeviceHasNoId() {
        assertNull(new TestDevice("Lamp", 60).getId());
    }

    @Test
    void constructorAcceptsZeroPowerUsage() {
        assertEquals(0, new TestDevice("Lamp", 0).getPowerUsage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void constructorRejectsNullOrBlankName(String name) {
        assertThrows(IllegalArgumentException.class, () -> new TestDevice(name, 10));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, -100, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void constructorRejectsInvalidPowerUsage(double powerUsage) {
        assertThrows(IllegalArgumentException.class, () -> new TestDevice("Lamp", powerUsage));
    }

    @Test
    void setIdChangesIdAndAcceptsNull() {
        Device device = new TestDevice("Lamp", 60);

        device.setId(7);
        assertEquals(7, device.getId());

        device.setId(null);
        assertNull(device.getId());
    }

    @Test
    void setNameChangesName() {
        Device device = new TestDevice("Lamp", 60);

        device.setName("Spot");

        assertEquals("Spot", device.getName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void setNameRejectsNullOrBlankAndKeepsOldName(String name) {
        Device device = new TestDevice("Lamp", 60);

        assertThrows(IllegalArgumentException.class, () -> device.setName(name));
        assertEquals("Lamp", device.getName());
    }

    @Test
    void setPowerUsageChangesPowerUsage() {
        Device device = new TestDevice("Lamp", 60);

        device.setPowerUsage(0);

        assertEquals(0, device.getPowerUsage());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1, Double.NaN, Double.POSITIVE_INFINITY})
    void setPowerUsageRejectsInvalidValueAndKeepsOldValue(double powerUsage) {
        Device device = new TestDevice("Lamp", 60);

        assertThrows(IllegalArgumentException.class, () -> device.setPowerUsage(powerUsage));
        assertEquals(60, device.getPowerUsage());
    }

    @Test
    void describeWithoutIdShowsNull() {
        assertEquals("| id: null || name: Lamp || type: Test |", new TestDevice("Lamp", 60).describe());
    }

    @Test
    void describeShowsIdNameAndType() {
        Device device = new TestDevice("Lamp", 60);
        device.setId(3);

        assertEquals("| id: 3 || name: Lamp || type: Test |", device.describe());
    }

    @Test
    void toStringReturnsDescribe() {
        Device device = new TestDevice("Lamp", 60);
        device.setId(3);

        assertEquals(device.describe(), device.toString());
    }
}
