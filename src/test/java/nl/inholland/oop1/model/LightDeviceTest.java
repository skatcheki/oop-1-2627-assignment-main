package nl.inholland.oop1.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LightDeviceTest {

    @Test
    void getTypeReturnsLight() {
        assertEquals("Light", new LightDevice("Lamp", 60).getType());
    }

    @Test
    void isADeviceAndDescribable() {
        LightDevice light = new LightDevice("Lamp", 60);

        assertInstanceOf(Device.class, light);
        assertInstanceOf(Describable.class, light);
    }

    @Test
    void constructorPassesValuesToDevice() {
        LightDevice light = new LightDevice("Lamp", 60);

        assertEquals("Lamp", light.getName());
        assertEquals(60, light.getPowerUsage());
    }

    @Test
    void constructorValidatesLikeDevice() {
        assertThrows(IllegalArgumentException.class, () -> new LightDevice("", 60));
        assertThrows(IllegalArgumentException.class, () -> new LightDevice("Lamp", -1));
    }

    @Test
    void describeUsesLightType() {
        Device light = new LightDevice("Lamp", 60);
        light.setId(1);

        assertEquals("| id: 1 || name: Lamp || type: Light |", light.describe());
        assertEquals(light.describe(), light.toString());
    }
}
