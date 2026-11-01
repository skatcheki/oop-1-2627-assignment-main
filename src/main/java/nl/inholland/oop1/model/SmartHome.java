package nl.inholland.oop1.model;

import nl.inholland.oop1.exception.DeviceNotFoundException;
import nl.inholland.oop1.exception.RoomNotEmptyException;
import nl.inholland.oop1.exception.RoomNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * The smart home: a collection of rooms with their devices.
 * <p>
 * Room names are unique and looked up case-insensitively. Device ids are unique across all rooms
 * and are assigned by {@link #addDeviceToRoom(String, Device)}.
 */
public class SmartHome {

    private static final Comparator<Device> BY_POWER_USAGE_DESC_THEN_NAME =
            Comparator.comparingDouble(Device::getPowerUsage).reversed()
                    .thenComparing(Device::getName, String.CASE_INSENSITIVE_ORDER);

    private final List<Room> rooms = new ArrayList<>();

    /**
     * Creates an empty smart home without rooms.
     */
    public SmartHome() {
        // Nothing to initialise: the home starts empty.
    }

    /**
     * Adds a room to the home.
     *
     * @param room the room to add; must not be {@code null}
     * @throws IllegalArgumentException if the room is {@code null} or a room with the same name
     *                                  (ignoring case) already exists
     */
    public void addRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room must not be null.");
        }
        if (rooms.stream().anyMatch(existing -> existing.getName().equalsIgnoreCase(room.getName()))) {
            throw new IllegalArgumentException("Room '" + room.getName() + "' already exists.");
        }
        rooms.add(room);
    }

    /**
     * Returns the rooms of this home in the order they were added.
     *
     * @return a read-only view of the rooms, never {@code null}
     */
    public List<Room> getRooms() {
        return Collections.unmodifiableList(rooms);
    }

    /**
     * Removes an empty room.
     *
     * @param roomName the name of the room (case-insensitive)
     * @throws RoomNotFoundException if no room has that name
     * @throws RoomNotEmptyException if the room still contains devices
     */
    public void removeRoom(String roomName) throws RoomNotFoundException, RoomNotEmptyException {
        Room room = findRoom(roomName);
        int deviceCount = room.getDevices().size();
        if (deviceCount > 0) {
            throw new RoomNotEmptyException(room.getName(), deviceCount);
        }
        rooms.remove(room);
    }

    /**
     * Adds a device to a room and gives it a new id: the highest existing id across all rooms + 1,
     * or 1 if the home has no devices. Any id the device already had is overwritten.
     *
     * @param roomName the name of the room (case-insensitive)
     * @param device   the device to add; must not be {@code null} or already be in this home
     * @return the id assigned to the device
     * @throws RoomNotFoundException    if no room has that name
     * @throws IllegalArgumentException if the device is {@code null} or already in this home
     */
    public int addDeviceToRoom(String roomName, Device device) throws RoomNotFoundException {
        if (device == null) {
            throw new IllegalArgumentException("Device must not be null.");
        }
        Room room = findRoom(roomName);
        List<Device> allDevices = getAllDevices();
        if (allDevices.stream().anyMatch(existing -> existing == device)) {
            throw new IllegalArgumentException("Device '" + device.getName() + "' is already in the home.");
        }
        int newId = allDevices.stream()
                .map(Device::getId)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0) + 1;
        device.setId(newId);
        room.addDevice(device);
        return newId;
    }

    /**
     * Removes the device with the given id from whichever room it is in.
     *
     * @param id the id of the device
     * @return {@code true} if a device was removed, {@code false} if no device has that id
     */
    public boolean removeDevice(int id) {
        for (Room room : rooms) {
            if (room.removeDevice(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all devices of all rooms, in room order and then in the order they were added.
     *
     * @return a new list, never {@code null}
     */
    public List<Device> getAllDevices() {
        return rooms.stream()
                .flatMap(room -> room.getDevices().stream())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns all devices that match the filter.
     *
     * @param filter decides which devices are returned; must not be {@code null}
     * @return a new list with the matching devices, never {@code null}
     * @throws NullPointerException if the filter is {@code null}
     */
    public List<Device> find(Selector<Device> filter) {
        Objects.requireNonNull(filter, "Filter must not be null.");
        return getAllDevices().stream()
                .filter(filter::select)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns all devices that are an instance of the given type (including subclasses).
     * For example {@code findByType(ThermostatDevice.class)} returns a {@code List<ThermostatDevice>}.
     *
     * @param type the class of the wanted devices; must not be {@code null}
     * @param <T>  the device type
     * @return a new list with the matching devices, never {@code null}
     * @throws NullPointerException if the type is {@code null}
     */
    public <T extends Device> List<T> findByType(Class<T> type) {
        Objects.requireNonNull(type, "Type must not be null.");
        return getAllDevices().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the device with the given id.
     *
     * @param id the id of the device
     * @return the device, never {@code null}
     * @throws DeviceNotFoundException if no device has that id
     */
    public Device getById(int id) throws DeviceNotFoundException {
        for (Device device : getAllDevices()) {
            if (device.getId() != null && device.getId() == id) {
                return device;
            }
        }
        throw new DeviceNotFoundException(id);
    }

    /**
     * Changes the target temperature of a thermostat.
     *
     * @param id     the id of the thermostat
     * @param target the new target temperature in °C
     * @throws DeviceNotFoundException  if no device has that id
     * @throws IllegalArgumentException if the device is not a thermostat or the target lies outside
     *                                  its range
     */
    public void setTargetTemperature(int id, double target) throws DeviceNotFoundException {
        Device device = getById(id);
        if (!(device instanceof ThermostatDevice thermostat)) {
            throw new IllegalArgumentException("Device with id '" + id + "' is not a thermostat.");
        }
        thermostat.setTargetTemperature(target);
    }

    /**
     * Returns all devices sorted by power usage, highest first. Devices with equal power usage are
     * sorted by name (case-insensitive).
     *
     * @return a new sorted list, never {@code null}
     */
    public List<Device> sortedByPowerUsage() {
        List<Device> devices = getAllDevices();
        devices.sort(BY_POWER_USAGE_DESC_THEN_NAME);
        return devices;
    }

    /**
     * Returns the sum of the power usage of all devices.
     *
     * @return the total power usage in watts, {@code 0.0} if there are no devices
     */
    public double totalPowerUsage() {
        return getAllDevices().stream()
                .mapToDouble(Device::getPowerUsage)
                .sum();
    }

    /**
     * Returns statistics about the devices that are currently in the home.
     *
     * @return a new snapshot; later changes to the home do not change it
     */
    public HomeStats stats() {
        return new HomeStats(getAllDevices());
    }

    private Room findRoom(String roomName) throws RoomNotFoundException {
        if (roomName != null) {
            String wanted = roomName.trim();
            for (Room room : rooms) {
                if (room.getName().equalsIgnoreCase(wanted)) {
                    return room;
                }
            }
        }
        throw new RoomNotFoundException(roomName);
    }

    /**
     * Snapshot of statistics about a collection of devices. The values are calculated once, in the
     * constructor.
     */
    public static class HomeStats {

        private final double averagePowerUsage;
        private final Map<String, Integer> countByType;

        /**
         * Calculates the statistics for the given devices.
         *
         * @param devices the devices to summarise; must not be {@code null}
         * @throws NullPointerException if devices is {@code null}
         */
        public HomeStats(Collection<Device> devices) {
            Objects.requireNonNull(devices, "Devices must not be null.");
            this.averagePowerUsage = devices.stream()
                    .mapToDouble(Device::getPowerUsage)
                    .average()
                    .orElse(0.0);
            this.countByType = Collections.unmodifiableMap(devices.stream()
                    .collect(Collectors.groupingBy(Device::getType, TreeMap::new, Collectors.summingInt(device -> 1))));
        }

        /**
         * Returns the arithmetic mean of the power usage.
         *
         * @return the average power usage in watts, {@code 0.0} if there were no devices
         */
        public double getAveragePowerUsage() {
            return averagePowerUsage;
        }

        /**
         * Returns the number of devices per type. Only types that occur are present; keys are
         * sorted alphabetically.
         *
         * @return a read-only map from type name to count, never {@code null}
         */
        public Map<String, Integer> getCountByType() {
            return countByType;
        }
    }
}
