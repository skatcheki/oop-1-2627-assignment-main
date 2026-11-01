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

import java.util.List;
import java.util.Objects;

/**
 * {@link SmartHomeService} that delegates to a {@link SmartHome}.
 * Exceptions of the smart home are passed on unchanged to the caller.
 */
public class SmartHomeServiceImpl implements SmartHomeService {

    private final SmartHome smartHome;

    /**
     * Creates the service for the given home.
     *
     * @param smartHome the home to manage; must not be {@code null}
     * @throws NullPointerException if smartHome is {@code null}
     */
    public SmartHomeServiceImpl(SmartHome smartHome) {
        this.smartHome = Objects.requireNonNull(smartHome, "SmartHome must not be null.");
    }

    @Override
    public List<Room> fetchAllRoomsAndDevices() {
        return smartHome.getRooms();
    }

    @Override
    public Device searchDeviceById(int id) throws DeviceNotFoundException {
        return smartHome.getById(id);
    }

    @Override
    public int addLightDevice(String roomName, LightDevice lightDevice) throws RoomNotFoundException {
        return smartHome.addDeviceToRoom(roomName, lightDevice);
    }

    @Override
    public int addThermostatDevice(String roomName, ThermostatDevice thermostatDevice) throws RoomNotFoundException {
        return smartHome.addDeviceToRoom(roomName, thermostatDevice);
    }

    @Override
    public boolean removeDevice(int id) {
        return smartHome.removeDevice(id);
    }

    @Override
    public void setTargetTemperature(int id, double target) throws DeviceNotFoundException {
        smartHome.setTargetTemperature(id, target);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Implemented with an anonymous {@link Selector} class (see {@code DECISIONS.md}).
     */
    @Override
    public List<Device> findDevicesWithPowerUsageLessThan(double max) {
        return smartHome.find(new Selector<Device>() {
            @Override
            public boolean select(Device device) {
                return device.getPowerUsage() < max;
            }
        });
    }

    @Override
    public List<Device> getDevicesSortedByPowerUsage() {
        return smartHome.sortedByPowerUsage();
    }

    @Override
    public double getTotalPowerUsage() {
        return smartHome.totalPowerUsage();
    }

    @Override
    public SmartHome.HomeStats getHomeStats() {
        return smartHome.stats();
    }

    @Override
    public void addRoom(String roomName) {
        smartHome.addRoom(new Room(roomName));
    }

    @Override
    public void removeRoom(String roomName) throws RoomNotFoundException, RoomNotEmptyException {
        smartHome.removeRoom(roomName);
    }
}
