package nl.inholland.oop1.service;

import nl.inholland.oop1.exception.DeviceNotFoundException;
import nl.inholland.oop1.exception.RoomNotEmptyException;
import nl.inholland.oop1.exception.RoomNotFoundException;
import nl.inholland.oop1.model.Device;
import nl.inholland.oop1.model.LightDevice;
import nl.inholland.oop1.model.Room;
import nl.inholland.oop1.model.SmartHome;
import nl.inholland.oop1.model.ThermostatDevice;

import java.util.List;

/**
 * The operations the user interface can perform on the smart home.
 */
public interface SmartHomeService {

    /**
     * Returns all rooms, each with its devices.
     *
     * @return the rooms in the order they were added, never {@code null}
     */
    List<Room> fetchAllRoomsAndDevices();

    /**
     * Looks up a device by its id.
     *
     * @param id the id of the device
     * @return the device, never {@code null}
     * @throws DeviceNotFoundException if no device has that id
     */
    Device searchDeviceById(int id) throws DeviceNotFoundException;

    /**
     * Adds a light to a room.
     *
     * @param roomName    the name of the room (case-insensitive)
     * @param lightDevice the light to add
     * @return the id assigned to the light
     * @throws RoomNotFoundException    if no room has that name
     * @throws IllegalArgumentException if the light is {@code null} or already in the home
     */
    int addLightDevice(String roomName, LightDevice lightDevice) throws RoomNotFoundException;

    /**
     * Adds a thermostat to a room.
     *
     * @param roomName         the name of the room (case-insensitive)
     * @param thermostatDevice the thermostat to add
     * @return the id assigned to the thermostat
     * @throws RoomNotFoundException    if no room has that name
     * @throws IllegalArgumentException if the thermostat is {@code null} or already in the home
     */
    int addThermostatDevice(String roomName, ThermostatDevice thermostatDevice) throws RoomNotFoundException;

    /**
     * Removes a device.
     *
     * @param id the id of the device
     * @return {@code true} if a device was removed, {@code false} if no device has that id
     */
    boolean removeDevice(int id);

    /**
     * Changes the target temperature of a thermostat.
     *
     * @param id     the id of the thermostat
     * @param target the new target temperature in °C
     * @throws DeviceNotFoundException  if no device has that id
     * @throws IllegalArgumentException if the device is not a thermostat or the target lies outside
     *                                  its range
     */
    void setTargetTemperature(int id, double target) throws DeviceNotFoundException;

    /**
     * Returns the devices whose power usage is strictly less than the given value.
     *
     * @param max the exclusive upper limit in watts
     * @return a new list with the matching devices, never {@code null}
     */
    List<Device> findDevicesWithPowerUsageLessThan(double max);

    /**
     * Returns all devices sorted by power usage, highest first, ties by name.
     *
     * @return a new sorted list, never {@code null}
     */
    List<Device> getDevicesSortedByPowerUsage();

    /**
     * Returns the total power usage of all devices.
     *
     * @return the total in watts, {@code 0.0} if there are no devices
     */
    double getTotalPowerUsage();

    /**
     * Returns a snapshot of statistics about the current devices.
     *
     * @return the statistics, never {@code null}
     */
    SmartHome.HomeStats getHomeStats();

    /**
     * Adds a new, empty room.
     *
     * @param roomName the name of the room
     * @throws IllegalArgumentException if the name is {@code null}, blank or already in use
     */
    void addRoom(String roomName);

    /**
     * Removes an empty room.
     *
     * @param roomName the name of the room (case-insensitive)
     * @throws RoomNotFoundException if no room has that name
     * @throws RoomNotEmptyException if the room still contains devices
     */
    void removeRoom(String roomName) throws RoomNotFoundException, RoomNotEmptyException;
}
