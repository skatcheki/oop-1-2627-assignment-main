package nl.inholland.oop1.exception;

/**
 * Thrown when no room with the requested name exists in the smart home.
 */
public class RoomNotFoundException extends Exception {

    /**
     * Creates the exception with the message {@code Room '<name>' not found.}
     *
     * @param roomName the room name that was not found
     */
    public RoomNotFoundException(String roomName) {
        super("Room '" + roomName + "' not found.");
    }
}
