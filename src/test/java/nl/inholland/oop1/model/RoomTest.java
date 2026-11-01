package nl.inholland.oop1.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomTest {

    private static Device lightWithId(String name, int id) {
        Device device = new LightDevice(name, 10);
        device.setId(id);
        return device;
    }

    @Test
    void constructorStoresName() {
        assertEquals("Kitchen", new Room("Kitchen").getName());
    }

    @Test
    void constructorTrimsName() {
        assertEquals("Living room", new Room("  Living room ").getName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void constructorRejectsNullOrBlankName(String name) {
        assertThrows(IllegalArgumentException.class, () -> new Room(name));
    }

    @Test
    void newRoomHasNoDevices() {
        assertTrue(new Room("Kitchen").getDevices().isEmpty());
    }

    @Test
    void addDeviceKeepsInsertionOrder() {
        Room room = new Room("Kitchen");
        Device first = lightWithId("A", 1);
        Device second = lightWithId("B", 2);

        room.addDevice(first);
        room.addDevice(second);

        assertEquals(List.of(first, second), room.getDevices());
    }

    @Test
    void addDeviceRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Room("Kitchen").addDevice(null));
    }

    @Test
    void getDevicesCannotBeModified() {
        Room room = new Room("Kitchen");
        room.addDevice(lightWithId("A", 1));
        List<Device> devices = room.getDevices();

        assertThrows(UnsupportedOperationException.class, () -> devices.add(lightWithId("B", 2)));
        assertThrows(UnsupportedOperationException.class, () -> devices.remove(0));
        assertThrows(UnsupportedOperationException.class, devices::clear);
        assertEquals(1, room.getDevices().size());
    }

    @Test
    void removeDeviceReturnsTrueAndRemovesOnlyThatDevice() {
        Room room = new Room("Kitchen");
        Device keep = lightWithId("A", 1);
        room.addDevice(keep);
        room.addDevice(lightWithId("B", 2));

        assertTrue(room.removeDevice(2));
        assertEquals(List.of(keep), room.getDevices());
    }

    @Test
    void removeDeviceReturnsFalseForUnknownId() {
        Room room = new Room("Kitchen");
        room.addDevice(lightWithId("A", 1));

        assertFalse(room.removeDevice(99));
        assertEquals(1, room.getDevices().size());
    }

    @Test
    void removeDeviceReturnsFalseInEmptyRoom() {
        assertFalse(new Room("Kitchen").removeDevice(1));
    }

    @Test
    void removeDeviceIgnoresDevicesWithoutId() {
        Room room = new Room("Kitchen");
        room.addDevice(new LightDevice("No id", 10));

        assertFalse(room.removeDevice(0));
        assertEquals(1, room.getDevices().size());
    }

    @Test
    void removeDeviceComparesIdsByValueNotReference() {
        // Integer values above 127 are not cached, so == on Integer objects would fail here.
        Room room = new Room("Kitchen");
        room.addDevice(lightWithId("A", 1000));

        assertTrue(room.removeDevice(1000));
    }
}
