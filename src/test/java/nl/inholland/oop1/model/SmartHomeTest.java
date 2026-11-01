package nl.inholland.oop1.model;

import nl.inholland.oop1.exception.DeviceNotFoundException;
import nl.inholland.oop1.exception.RoomNotEmptyException;
import nl.inholland.oop1.exception.RoomNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SmartHomeTest {

    private SmartHome home;

    @BeforeEach
    void setUp() {
        home = new SmartHome();
    }

    /** Adds a kitchen with a 60 W lamp (id 1) and a living room with a 5 W thermostat (id 2). */
    private void addKitchenAndLivingRoom() throws RoomNotFoundException {
        home.addRoom(new Room("Kitchen"));
        home.addRoom(new Room("Living room"));
        home.addDeviceToRoom("Kitchen", new LightDevice("Lamp", 60));
        home.addDeviceToRoom("Living room", new ThermostatDevice("Thermo", 5, "16-24"));
    }

    // ---------- rooms ----------

    @Test
    void newHomeIsEmpty() {
        assertTrue(home.getRooms().isEmpty());
        assertTrue(home.getAllDevices().isEmpty());
    }

    @Test
    void addRoomKeepsInsertionOrder() {
        Room kitchen = new Room("Kitchen");
        Room hall = new Room("Hall");

        home.addRoom(kitchen);
        home.addRoom(hall);

        assertEquals(List.of(kitchen, hall), home.getRooms());
    }

    @Test
    void addRoomRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> home.addRoom(null));
    }

    @Test
    void addRoomRejectsDuplicateNameIgnoringCase() {
        home.addRoom(new Room("Kitchen"));

        assertThrows(IllegalArgumentException.class, () -> home.addRoom(new Room("kitchen")));
        assertThrows(IllegalArgumentException.class, () -> home.addRoom(new Room(" KITCHEN ")));
        assertEquals(1, home.getRooms().size());
    }

    @Test
    void getRoomsCannotBeModified() {
        List<Room> rooms = home.getRooms();

        assertThrows(UnsupportedOperationException.class, () -> rooms.add(new Room("Hall")));
    }

    @Test
    void removeRoomRemovesEmptyRoom() throws Exception {
        home.addRoom(new Room("Kitchen"));

        home.removeRoom("Kitchen");

        assertTrue(home.getRooms().isEmpty());
    }

    @Test
    void removeRoomFindsNameIgnoringCaseAndWhitespace() {
        home.addRoom(new Room("Kitchen"));

        assertDoesNotThrow(() -> home.removeRoom("  kITCHEN "));
        assertTrue(home.getRooms().isEmpty());
    }

    @Test
    void removeRoomThrowsWhenRoomNotFound() {
        RoomNotFoundException e = assertThrows(RoomNotFoundException.class, () -> home.removeRoom("Attic"));
        assertEquals("Room 'Attic' not found.", e.getMessage());
    }

    @Test
    void removeRoomThrowsWhenNameIsNull() {
        assertThrows(RoomNotFoundException.class, () -> home.removeRoom(null));
    }

    @Test
    void removeRoomThrowsWhenRoomStillHasDevices() throws Exception {
        home.addRoom(new Room("Kitchen"));
        home.addDeviceToRoom("Kitchen", new LightDevice("A", 10));
        home.addDeviceToRoom("Kitchen", new LightDevice("B", 10));

        RoomNotEmptyException e = assertThrows(RoomNotEmptyException.class, () -> home.removeRoom("kitchen"));
        assertEquals("Room 'Kitchen' still contains 2 device(s).", e.getMessage());
        assertEquals(1, home.getRooms().size());
    }

    // ---------- adding devices / ids ----------

    @Test
    void firstDeviceInEmptyHomeGetsIdOne() throws Exception {
        home.addRoom(new Room("Kitchen"));
        Device lamp = new LightDevice("Lamp", 60);

        int id = home.addDeviceToRoom("Kitchen", lamp);

        assertEquals(1, id);
        assertEquals(1, lamp.getId());
    }

    @Test
    void newIdIsHighestIdAcrossAllRoomsPlusOne() throws Exception {
        home.addRoom(new Room("Kitchen"));
        home.addRoom(new Room("Hall"));
        home.addDeviceToRoom("Kitchen", new LightDevice("A", 10));   // 1
        home.addDeviceToRoom("Hall", new LightDevice("B", 10));      // 2
        home.addDeviceToRoom("Kitchen", new LightDevice("C", 10));   // 3
        home.removeDevice(1);

        assertEquals(4, home.addDeviceToRoom("Hall", new LightDevice("D", 10)));
    }

    @Test
    void idOfRemovedHighestDeviceIsReused() throws Exception {
        home.addRoom(new Room("Kitchen"));
        home.addDeviceToRoom("Kitchen", new LightDevice("A", 10));   // 1
        home.addDeviceToRoom("Kitchen", new LightDevice("B", 10));   // 2
        home.removeDevice(2);

        assertEquals(2, home.addDeviceToRoom("Kitchen", new LightDevice("C", 10)));
    }

    @Test
    void preSetIdIsIgnored() throws Exception {
        home.addRoom(new Room("Kitchen"));
        Device lamp = new LightDevice("Lamp", 60);
        lamp.setId(99);

        assertEquals(1, home.addDeviceToRoom("Kitchen", lamp));
        assertEquals(1, lamp.getId());
    }

    @Test
    void addDeviceToRoomUsesRoomNameIgnoringCase() throws Exception {
        home.addRoom(new Room("Kitchen"));
        Device lamp = new LightDevice("Lamp", 60);

        home.addDeviceToRoom("KITCHEN", lamp);

        assertEquals(List.of(lamp), home.getRooms().getFirst().getDevices());
    }

    @Test
    void addDeviceToUnknownRoomThrowsAndLeavesIdUnchanged() {
        Device lamp = new LightDevice("Lamp", 60);

        RoomNotFoundException e = assertThrows(RoomNotFoundException.class,
                () -> home.addDeviceToRoom("Attic", lamp));
        assertEquals("Room 'Attic' not found.", e.getMessage());
        assertNull(lamp.getId());
    }

    @Test
    void addNullDeviceThrows() {
        home.addRoom(new Room("Kitchen"));

        assertThrows(IllegalArgumentException.class, () -> home.addDeviceToRoom("Kitchen", null));
    }

    @Test
    void addSameDeviceTwiceThrows() throws Exception {
        home.addRoom(new Room("Kitchen"));
        home.addRoom(new Room("Hall"));
        Device lamp = new LightDevice("Lamp", 60);
        home.addDeviceToRoom("Kitchen", lamp);

        assertThrows(IllegalArgumentException.class, () -> home.addDeviceToRoom("Hall", lamp));
        assertThrows(IllegalArgumentException.class, () -> home.addDeviceToRoom("Kitchen", lamp));
        assertEquals(1, lamp.getId());
        assertEquals(1, home.getAllDevices().size());
    }

    @Test
    void devicesWithSameValuesAreDifferentDevices() throws Exception {
        home.addRoom(new Room("Kitchen"));
        home.addDeviceToRoom("Kitchen", new LightDevice("Lamp", 60));

        assertEquals(2, home.addDeviceToRoom("Kitchen", new LightDevice("Lamp", 60)));
    }

    // ---------- removing / looking up devices ----------

    @Test
    void removeDeviceReturnsTrueAndRemovesFromItsRoom() throws Exception {
        addKitchenAndLivingRoom();

        assertTrue(home.removeDevice(2));
        assertTrue(home.getRooms().get(1).getDevices().isEmpty());
        assertEquals(1, home.getAllDevices().size());
    }

    @Test
    void removeDeviceReturnsFalseForUnknownId() throws Exception {
        addKitchenAndLivingRoom();

        assertFalse(home.removeDevice(42));
        assertEquals(2, home.getAllDevices().size());
    }

    @Test
    void removeDeviceReturnsFalseInEmptyHome() {
        assertFalse(home.removeDevice(1));
    }

    @Test
    void getByIdReturnsDevice() throws Exception {
        home.addRoom(new Room("Kitchen"));
        Device lamp = new LightDevice("Lamp", 60);
        int id = home.addDeviceToRoom("Kitchen", lamp);

        assertSame(lamp, home.getById(id));
    }

    @Test
    void getByIdThrowsForUnknownId() throws Exception {
        addKitchenAndLivingRoom();

        DeviceNotFoundException e = assertThrows(DeviceNotFoundException.class, () -> home.getById(7));
        assertEquals("Device with id '7' not found.", e.getMessage());
    }

    @Test
    void getByIdSkipsDeviceWhoseIdWasClearedAfterAdding() throws Exception {
        home.addRoom(new Room("Kitchen"));
        Device lamp = new LightDevice("Lamp", 60);
        home.addDeviceToRoom("Kitchen", lamp);
        lamp.setId(null);

        assertThrows(DeviceNotFoundException.class, () -> home.getById(1));
    }

    @Test
    void getByIdWorksForIdsAbove127() throws Exception {
        home.addRoom(new Room("Kitchen"));
        for (int i = 0; i < 200; i++) {
            home.addDeviceToRoom("Kitchen", new LightDevice("L" + i, 1));
        }

        assertEquals("L199", home.getById(200).getName());
    }

    // ---------- listing / querying ----------

    @Test
    void getAllDevicesReturnsDevicesInRoomOrder() throws Exception {
        addKitchenAndLivingRoom();

        List<Device> devices = home.getAllDevices();

        assertEquals(List.of(1, 2), devices.stream().map(Device::getId).toList());
    }

    @Test
    void getAllDevicesReturnsNewList() throws Exception {
        addKitchenAndLivingRoom();

        List<Device> devices = home.getAllDevices();
        devices.clear();

        assertNotSame(devices, home.getAllDevices());
        assertEquals(2, home.getAllDevices().size());
    }

    @Test
    void findReturnsMatchingDevicesWithLambda() throws Exception {
        addKitchenAndLivingRoom();

        List<Device> result = home.find(device -> device.getPowerUsage() > 10);

        assertEquals(1, result.size());
        assertEquals("Lamp", result.getFirst().getName());
    }

    @Test
    void findReturnsEmptyListWhenNothingMatches() throws Exception {
        addKitchenAndLivingRoom();

        assertTrue(home.find(device -> false).isEmpty());
    }

    @Test
    void findThrowsNullPointerExceptionForNullFilter() {
        assertThrows(NullPointerException.class, () -> home.find(null));
    }

    @Test
    void findByTypeReturnsTypedList() throws Exception {
        addKitchenAndLivingRoom();

        List<ThermostatDevice> thermostats = home.findByType(ThermostatDevice.class);
        List<LightDevice> lights = home.findByType(LightDevice.class);

        assertEquals(1, thermostats.size());
        assertEquals(16, thermostats.getFirst().getTargetTemperature());
        assertEquals("Lamp", lights.getFirst().getName());
    }

    @Test
    void findByTypeWithDeviceClassReturnsAllDevices() throws Exception {
        addKitchenAndLivingRoom();

        assertEquals(2, home.findByType(Device.class).size());
    }

    @Test
    void findByTypeReturnsEmptyListWhenNoDeviceOfType() throws Exception {
        home.addRoom(new Room("Kitchen"));
        home.addDeviceToRoom("Kitchen", new LightDevice("Lamp", 60));

        assertTrue(home.findByType(ThermostatDevice.class).isEmpty());
    }

    @Test
    void findByTypeThrowsNullPointerExceptionForNullType() {
        assertThrows(NullPointerException.class, () -> home.findByType(null));
    }

    // ---------- thermostat ----------

    @Test
    void setTargetTemperatureChangesThermostat() throws Exception {
        addKitchenAndLivingRoom();

        home.setTargetTemperature(2, 21);

        assertEquals(21, home.findByType(ThermostatDevice.class).getFirst().getTargetTemperature());
    }

    @Test
    void setTargetTemperatureOnLightThrowsIllegalArgumentException() throws Exception {
        addKitchenAndLivingRoom();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> home.setTargetTemperature(1, 20));
        assertEquals("Device with id '1' is not a thermostat.", e.getMessage());
    }

    @Test
    void setTargetTemperatureOutsideRangeThrows() throws Exception {
        addKitchenAndLivingRoom();

        assertThrows(IllegalArgumentException.class, () -> home.setTargetTemperature(2, 30));
    }

    @Test
    void setTargetTemperatureOnUnknownIdThrowsDeviceNotFound() {
        assertThrows(DeviceNotFoundException.class, () -> home.setTargetTemperature(1, 20));
    }

    // ---------- sorting / totals / stats ----------

    @Test
    void sortedByPowerUsageHighestFirstAndTiesByNameIgnoringCase() throws Exception {
        home.addRoom(new Room("Kitchen"));
        home.addRoom(new Room("Hall"));
        home.addDeviceToRoom("Kitchen", new LightDevice("small", 5));
        home.addDeviceToRoom("Kitchen", new LightDevice("zeta", 100));
        home.addDeviceToRoom("Hall", new LightDevice("Beta", 100));
        home.addDeviceToRoom("Hall", new LightDevice("alpha", 100));
        home.addDeviceToRoom("Hall", new ThermostatDevice("Medium", 50, "16-24"));

        List<String> names = home.sortedByPowerUsage().stream().map(Device::getName).toList();

        assertEquals(List.of("alpha", "Beta", "zeta", "Medium", "small"), names);
    }

    @Test
    void sortedByPowerUsageDoesNotChangeRoomOrder() throws Exception {
        home.addRoom(new Room("Kitchen"));
        home.addDeviceToRoom("Kitchen", new LightDevice("Low", 1));
        home.addDeviceToRoom("Kitchen", new LightDevice("High", 100));

        home.sortedByPowerUsage();

        assertEquals("Low", home.getAllDevices().getFirst().getName());
    }

    @Test
    void sortedByPowerUsageOfEmptyHomeIsEmpty() {
        assertTrue(home.sortedByPowerUsage().isEmpty());
    }

    @Test
    void totalPowerUsageSumsAllRooms() throws Exception {
        addKitchenAndLivingRoom();

        assertEquals(65, home.totalPowerUsage(), 1e-9);
    }

    @Test
    void totalPowerUsageOfEmptyHomeIsZero() {
        assertEquals(0.0, home.totalPowerUsage());
    }

    @Test
    void statsReflectsCurrentDevices() throws Exception {
        addKitchenAndLivingRoom();

        SmartHome.HomeStats stats = home.stats();

        assertEquals(32.5, stats.getAveragePowerUsage(), 1e-9);
        assertEquals(Map.of("Light", 1, "Thermostat", 1), stats.getCountByType());
    }

    @Test
    void statsIsASnapshot() throws Exception {
        addKitchenAndLivingRoom();
        SmartHome.HomeStats before = home.stats();

        home.removeDevice(1);

        assertEquals(Map.of("Light", 1, "Thermostat", 1), before.getCountByType());
        assertEquals(Map.of("Thermostat", 1), home.stats().getCountByType());
    }

    @Test
    void statsOfEmptyHome() {
        SmartHome.HomeStats stats = home.stats();

        assertEquals(0.0, stats.getAveragePowerUsage());
        assertTrue(stats.getCountByType().isEmpty());
    }
}
