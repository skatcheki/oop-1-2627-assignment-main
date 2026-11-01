package nl.inholland.oop1.exception;

/**
 * Thrown when no device with the requested id exists in the smart home.
 */
public class DeviceNotFoundException extends Exception {

    /**
     * Creates the exception with the message {@code Device with id '<id>' not found.}
     *
     * @param id the id that was not found
     */
    public DeviceNotFoundException(int id) {
        super("Device with id '" + id + "' not found.");
    }
}
