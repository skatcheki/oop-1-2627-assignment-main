package nl.inholland.oop1.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A room in the smart home that holds devices.
 */
public class Room {

    private final String name;
    private final List<Device> devices = new ArrayList<>();

    /**
     * Creates an empty room.
     *
     * @param name the name of the room; must not be {@code null} or blank.
     *             Surrounding whitespace is removed.
     * @throws IllegalArgumentException if the name is {@code null} or blank
     */
    public Room(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Room name must not be empty.");
        }
        this.name = name.trim();
    }

    /**
     * Returns the name of this room.
     *
     * @return the trimmed name, never {@code null} or blank
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the devices in this room, in the order they were added.
     *
     * @return a read-only view of the devices; changes through this list throw
     *         {@link UnsupportedOperationException}
     */
    public List<Device> getDevices() {
        return Collections.unmodifiableList(devices);
    }

    /**
     * Adds a device to this room.
     *
     * @param device the device to add; must not be {@code null}
     * @throws IllegalArgumentException if the device is {@code null}
     */
    public void addDevice(Device device) {
        if (device == null) {
            throw new IllegalArgumentException("Device must not be null.");
        }
        devices.add(device);
    }

    /**
     * Removes the device with the given id from this room.
     *
     * @param id the id of the device to remove
     * @return {@code true} if a device was removed, {@code false} if no device has that id
     */
    public boolean removeDevice(int id) {
        return devices.removeIf(device -> device.getId() != null && device.getId() == id);
    }
}
