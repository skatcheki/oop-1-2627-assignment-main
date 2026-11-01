package nl.inholland.oop1.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HomeStatsTest {

    @Test
    void averageOfNoDevicesIsZero() {
        assertEquals(0.0, new SmartHome.HomeStats(List.of()).getAveragePowerUsage());
    }

    @Test
    void countByTypeOfNoDevicesIsEmpty() {
        assertTrue(new SmartHome.HomeStats(List.of()).getCountByType().isEmpty());
    }

    @Test
    void averageIsArithmeticMean() {
        SmartHome.HomeStats stats = new SmartHome.HomeStats(List.of(
                new LightDevice("A", 10),
                new LightDevice("B", 20),
                new ThermostatDevice("C", 0, "16-24")));

        assertEquals(10.0, stats.getAveragePowerUsage(), 1e-9);
    }

    @Test
    void averageOfSingleDeviceIsItsPowerUsage() {
        assertEquals(42.5, new SmartHome.HomeStats(List.of(new LightDevice("A", 42.5))).getAveragePowerUsage());
    }

    @Test
    void countByTypeCountsEachType() {
        SmartHome.HomeStats stats = new SmartHome.HomeStats(List.of(
                new LightDevice("A", 10),
                new ThermostatDevice("B", 5, "16-24"),
                new LightDevice("C", 10)));

        assertEquals(Map.of("Light", 2, "Thermostat", 1), stats.getCountByType());
    }

    @Test
    void countByTypeOnlyContainsTypesThatOccur() {
        SmartHome.HomeStats stats = new SmartHome.HomeStats(List.of(new LightDevice("A", 10)));

        assertFalse(stats.getCountByType().containsKey("Thermostat"));
    }

    @Test
    void countByTypeKeysAreSortedAlphabetically() {
        SmartHome.HomeStats stats = new SmartHome.HomeStats(List.of(
                new ThermostatDevice("B", 5, "16-24"),
                new LightDevice("A", 10)));

        assertEquals(List.of("Light", "Thermostat"), new ArrayList<>(stats.getCountByType().keySet()));
    }

    @Test
    void countByTypeCannotBeModified() {
        Map<String, Integer> counts = new SmartHome.HomeStats(List.of(new LightDevice("A", 10))).getCountByType();

        assertThrows(UnsupportedOperationException.class, () -> counts.put("Light", 99));
    }

    @Test
    void statsAreNotAffectedByLaterChangesToTheCollection() {
        List<Device> devices = new ArrayList<>(List.of(new LightDevice("A", 10)));
        SmartHome.HomeStats stats = new SmartHome.HomeStats(devices);

        devices.add(new LightDevice("B", 30));

        assertEquals(10.0, stats.getAveragePowerUsage());
        assertEquals(Map.of("Light", 1), stats.getCountByType());
    }

    @Test
    void acceptsAnyCollection() {
        SmartHome.HomeStats stats = new SmartHome.HomeStats(Set.of(new LightDevice("A", 10)));

        assertEquals(Map.of("Light", 1), stats.getCountByType());
    }

    @Test
    void constructorRejectsNull() {
        assertThrows(NullPointerException.class, () -> new SmartHome.HomeStats(null));
    }
}
