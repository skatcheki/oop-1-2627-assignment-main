package nl.inholland.oop1.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests the messages of the custom exceptions and that they are checked exceptions.
 */
class ExceptionTest {

    @Test
    void deviceNotFoundMessage() {
        assertEquals("Device with id '42' not found.", new DeviceNotFoundException(42).getMessage());
    }

    @Test
    void roomNotFoundMessage() {
        assertEquals("Room 'Attic' not found.", new RoomNotFoundException("Attic").getMessage());
    }

    @Test
    void roomNotEmptyMessage() {
        assertEquals("Room 'Kitchen' still contains 3 device(s).",
                new RoomNotEmptyException("Kitchen", 3).getMessage());
    }

    @Test
    void roomNotEmptyMessageWithOneDevice() {
        assertEquals("Room 'Kitchen' still contains 1 device(s).",
                new RoomNotEmptyException("Kitchen", 1).getMessage());
    }

    @ParameterizedTest
    @ValueSource(classes = {DeviceNotFoundException.class, RoomNotFoundException.class, RoomNotEmptyException.class})
    void allCustomExceptionsAreChecked(Class<?> exceptionClass) {
        assertEquals(Exception.class, exceptionClass.getSuperclass());
        assertFalse(RuntimeException.class.isAssignableFrom(exceptionClass));
    }
}
