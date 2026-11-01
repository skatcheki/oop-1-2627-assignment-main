package nl.inholland.oop1.service;

import nl.inholland.oop1.exception.DeviceNotFoundException;
import nl.inholland.oop1.exception.RoomNotEmptyException;
import nl.inholland.oop1.exception.RoomNotFoundException;
import nl.inholland.oop1.model.Device;
import nl.inholland.oop1.model.LightDevice;
import nl.inholland.oop1.model.Room;
import nl.inholland.oop1.model.Selector;
import nl.inholland.oop1.model.SmartHome;
import nl.inholland.oop1.model.ThermostatDevice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests {@link SmartHomeServiceImpl} in isolation: the {@link SmartHome} is a Mockito mock.
 */
@ExtendWith(MockitoExtension.class)
class SmartHomeServiceImplTest {

    @Mock
    private SmartHome smartHome;

    private SmartHomeService service;

    @BeforeEach
    void setUp() {
        service = new SmartHomeServiceImpl(smartHome);
    }

    @Test
    void constructorRejectsNull() {
        assertThrows(NullPointerException.class, () -> new SmartHomeServiceImpl(null));
    }

    @Test
    void fetchAllRoomsAndDevicesReturnsRoomsOfHome() {
        List<Room> rooms = List.of(new Room("Kitchen"));
        when(smartHome.getRooms()).thenReturn(rooms);

        assertSame(rooms, service.fetchAllRoomsAndDevices());
    }

    @Test
    void searchDeviceByIdReturnsDevice() throws Exception {
        Device lamp = new LightDevice("Lamp", 60);
        when(smartHome.getById(3)).thenReturn(lamp);

        assertSame(lamp, service.searchDeviceById(3));
    }

    @Test
    void searchDeviceByIdPassesOnDeviceNotFound() throws Exception {
        when(smartHome.getById(3)).thenThrow(new DeviceNotFoundException(3));

        DeviceNotFoundException e = assertThrows(DeviceNotFoundException.class, () -> service.searchDeviceById(3));
        assertEquals("Device with id '3' not found.", e.getMessage());
    }

    @Test
    void addLightDeviceDelegatesAndReturnsId() throws Exception {
        LightDevice lamp = new LightDevice("Lamp", 60);
        when(smartHome.addDeviceToRoom("Kitchen", lamp)).thenReturn(4);

        assertEquals(4, service.addLightDevice("Kitchen", lamp));
    }

    @Test
    void addLightDevicePassesOnRoomNotFound() throws Exception {
        LightDevice lamp = new LightDevice("Lamp", 60);
        when(smartHome.addDeviceToRoom("Attic", lamp)).thenThrow(new RoomNotFoundException("Attic"));

        assertThrows(RoomNotFoundException.class, () -> service.addLightDevice("Attic", lamp));
    }

    @Test
    void addThermostatDeviceDelegatesAndReturnsId() throws Exception {
        ThermostatDevice thermostat = new ThermostatDevice("Hall", 5, "16-24");
        when(smartHome.addDeviceToRoom("Hall", thermostat)).thenReturn(7);

        assertEquals(7, service.addThermostatDevice("Hall", thermostat));
    }

    @Test
    void addThermostatDevicePassesOnRoomNotFound() throws Exception {
        ThermostatDevice thermostat = new ThermostatDevice("Hall", 5, "16-24");
        when(smartHome.addDeviceToRoom("Attic", thermostat)).thenThrow(new RoomNotFoundException("Attic"));

        assertThrows(RoomNotFoundException.class, () -> service.addThermostatDevice("Attic", thermostat));
    }

    @Test
    void removeDeviceReturnsResultOfHome() {
        when(smartHome.removeDevice(1)).thenReturn(true);
        when(smartHome.removeDevice(2)).thenReturn(false);

        assertTrue(service.removeDevice(1));
        assertFalse(service.removeDevice(2));
    }

    @Test
    void setTargetTemperatureDelegates() throws Exception {
        service.setTargetTemperature(2, 21.5);

        verify(smartHome).setTargetTemperature(2, 21.5);
    }

    @Test
    void setTargetTemperaturePassesOnExceptions() throws Exception {
        doThrow(new DeviceNotFoundException(9)).when(smartHome).setTargetTemperature(9, 20);
        doThrow(new IllegalArgumentException("not a thermostat")).when(smartHome).setTargetTemperature(1, 20);

        assertThrows(DeviceNotFoundException.class, () -> service.setTargetTemperature(9, 20));
        assertThrows(IllegalArgumentException.class, () -> service.setTargetTemperature(1, 20));
    }

    @Test
    void findDevicesWithPowerUsageLessThanReturnsResultOfFind() {
        List<Device> found = List.of(new LightDevice("Lamp", 5));
        when(smartHome.find(any())).thenReturn(found);

        assertSame(found, service.findDevicesWithPowerUsageLessThan(10));
    }

    @Test
    @SuppressWarnings("unchecked")
    void findDevicesWithPowerUsageLessThanUsesStrictLessThan() {
        ArgumentCaptor<Selector<Device>> captor = ArgumentCaptor.forClass(Selector.class);
        when(smartHome.find(captor.capture())).thenReturn(List.of());

        service.findDevicesWithPowerUsageLessThan(10);
        Selector<Device> selector = captor.getValue();

        assertTrue(selector.select(new LightDevice("Low", 9.99)));
        assertTrue(selector.select(new LightDevice("Zero", 0)));
        assertFalse(selector.select(new LightDevice("Equal", 10)));
        assertFalse(selector.select(new LightDevice("High", 10.01)));
    }

    @Test
    @SuppressWarnings("unchecked")
    void findDevicesWithNegativeOrNaNMaxSelectsNothing() {
        ArgumentCaptor<Selector<Device>> captor = ArgumentCaptor.forClass(Selector.class);
        when(smartHome.find(captor.capture())).thenReturn(List.of());

        service.findDevicesWithPowerUsageLessThan(-1);
        service.findDevicesWithPowerUsageLessThan(Double.NaN);

        Device zero = new LightDevice("Zero", 0);
        assertFalse(captor.getAllValues().get(0).select(zero));
        assertFalse(captor.getAllValues().get(1).select(zero));
    }

    @Test
    void getDevicesSortedByPowerUsageReturnsResultOfHome() {
        List<Device> sorted = List.of(new LightDevice("A", 100), new LightDevice("B", 1));
        when(smartHome.sortedByPowerUsage()).thenReturn(sorted);

        assertSame(sorted, service.getDevicesSortedByPowerUsage());
    }

    @Test
    void getTotalPowerUsageReturnsResultOfHome() {
        when(smartHome.totalPowerUsage()).thenReturn(123.5);

        assertEquals(123.5, service.getTotalPowerUsage());
    }

    @Test
    void getHomeStatsReturnsResultOfHome() {
        SmartHome.HomeStats stats = new SmartHome.HomeStats(List.of());
        when(smartHome.stats()).thenReturn(stats);

        assertSame(stats, service.getHomeStats());
    }

    @Test
    void addRoomCreatesRoomWithGivenName() {
        service.addRoom("Kitchen");

        verify(smartHome).addRoom(argThat(room -> room.getName().equals("Kitchen")));
    }

    @Test
    void addRoomWithBlankNameThrowsAndDoesNotCallHome() {
        assertThrows(IllegalArgumentException.class, () -> service.addRoom(" "));
        assertThrows(IllegalArgumentException.class, () -> service.addRoom(null));

        verify(smartHome, never()).addRoom(any());
    }

    @Test
    void addRoomPassesOnDuplicateName() {
        doThrow(new IllegalArgumentException("exists")).when(smartHome).addRoom(any());

        assertThrows(IllegalArgumentException.class, () -> service.addRoom("Kitchen"));
    }

    @Test
    void removeRoomDelegates() throws Exception {
        service.removeRoom("Kitchen");

        verify(smartHome).removeRoom("Kitchen");
    }

    @Test
    void removeRoomPassesOnExceptions() throws Exception {
        doThrow(new RoomNotFoundException("Attic")).when(smartHome).removeRoom("Attic");
        doThrow(new RoomNotEmptyException("Kitchen", 2)).when(smartHome).removeRoom("Kitchen");

        assertThrows(RoomNotFoundException.class, () -> service.removeRoom("Attic"));
        RoomNotEmptyException e = assertThrows(RoomNotEmptyException.class, () -> service.removeRoom("Kitchen"));
        assertEquals("Room 'Kitchen' still contains 2 device(s).", e.getMessage());
    }
}
