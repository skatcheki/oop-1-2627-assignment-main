package nl.inholland.oop1.exception;

/**
 * Thrown when a room is removed while it still contains devices.
 */
public class RoomNotEmptyException extends Exception {

    /**
     * Creates the exception with the message {@code Room '<name>' still contains <n> device(s).}
     *
     * @param roomName    the name of the room that could not be removed
     * @param deviceCount the number of devices still in the room
     */
    public RoomNotEmptyException(String roomName, int deviceCount) {
        super("Room '" + roomName + "' still contains " + deviceCount + " device(s).");
    }
}
