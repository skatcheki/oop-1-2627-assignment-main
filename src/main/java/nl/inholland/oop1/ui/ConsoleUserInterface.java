package nl.inholland.oop1.ui;

import nl.inholland.oop1.exception.DeviceNotFoundException;
import nl.inholland.oop1.exception.RoomNotEmptyException;
import nl.inholland.oop1.exception.RoomNotFoundException;
import nl.inholland.oop1.model.Device;
import nl.inholland.oop1.model.LightDevice;
import nl.inholland.oop1.model.Room;
import nl.inholland.oop1.model.SmartHome;
import nl.inholland.oop1.model.ThermostatDevice;
import nl.inholland.oop1.service.SmartHomeService;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

/**
 * Text menu for managing the smart home from the console.
 * <p>
 * Input is always read as whole lines and parsed here, so wrong input (text instead of a number,
 * an unknown id, an invalid range, ...) never crashes the application: a message is shown and the
 * menu is displayed again.
 */
public class ConsoleUserInterface {

    private final SmartHomeService smartHomeService;
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Creates the user interface.
     *
     * @param smartHomeService the service that performs the actions chosen by the user
     */
    public ConsoleUserInterface(SmartHomeService smartHomeService) {
        this.smartHomeService = smartHomeService;
    }

    /**
     * Shows the menu and handles choices until the user chooses 0 (or the input ends).
     */
    public void start() {
        System.out.println("Welcome to the Smart House Application!");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = readLine("Enter choice: ");
            if (choice == null) {
                // End of input (e.g. Ctrl+D / Ctrl+Z): stop instead of looping forever.
                System.out.println();
                break;
            }
            running = handleChoice(choice.trim());
        }
        System.out.println("Goodbye!");
    }

    private void printMenu() {
        System.out.println("What do you want to do?");
        System.out.println("1. View all rooms and their devices");
        System.out.println("2. Search for a device (by id)");
        System.out.println("3. Add a light device");
        System.out.println("4. Add a thermostat device");
        System.out.println("5. Remove a device (by id)");
        System.out.println("6. Find devices whose power usage is less than a value");
        System.out.println("7. Total power usage of all devices");
        System.out.println("8. Stats (avg power usage and count by type)");
        System.out.println("9. Add a room (by name)");
        System.out.println("10. Remove a room (by name)");
        System.out.println("0. Exit");
    }

    /**
     * Executes one menu choice.
     *
     * @return {@code false} if the user wants to exit
     */
    private boolean handleChoice(String choice) {
        if (choice.equals("0")) {
            return false;
        }
        try {
            switch (choice) {
                case "1" -> viewAllRoomsAndDevices();
                case "2" -> searchDevice();
                case "3" -> addLightDevice();
                case "4" -> addThermostatDevice();
                case "5" -> removeDevice();
                case "6" -> findDevicesWithPowerUsageLessThan();
                case "7" -> showTotalPowerUsage();
                case "8" -> showStats();
                case "9" -> addRoom();
                case "10" -> removeRoom();
                default -> System.out.println("Invalid choice '" + choice + "'. Please enter a number from 0 to 10.");
            }
        } catch (DeviceNotFoundException | RoomNotFoundException | RoomNotEmptyException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        } catch (InputEndedException e) {
            System.out.println();
            return false;
        } catch (RuntimeException e) {
            // Last line of defence: the menu must never crash.
            System.out.println("Unexpected error: " + e.getMessage());
        } finally {
            System.out.println();
        }
        return true;
    }

    private void viewAllRoomsAndDevices() {
        List<Room> rooms = smartHomeService.fetchAllRoomsAndDevices();
        if (rooms.isEmpty()) {
            System.out.println("There are no rooms yet. Add one with option 9.");
            return;
        }
        for (Room room : rooms) {
            System.out.println("Room: " + room.getName() + " (" + room.getDevices().size() + " device(s))");
            if (room.getDevices().isEmpty()) {
                System.out.println("  (no devices)");
            }
            room.getDevices().forEach(this::printDevice);
        }
    }

    private void searchDevice() throws DeviceNotFoundException {
        int id = readInt("Device id: ");
        printDevice(smartHomeService.searchDeviceById(id));
    }

    private void addLightDevice() throws RoomNotFoundException {
        String roomName = readRequiredLine("Room name: ");
        String name = readRequiredLine("Device name: ");
        double powerUsage = readDouble("Power usage (W): ");
        int id = smartHomeService.addLightDevice(roomName, new LightDevice(name, powerUsage));
        System.out.println("Light device added with id " + id + ".");
    }

    private void addThermostatDevice() throws RoomNotFoundException, DeviceNotFoundException {
        String roomName = readRequiredLine("Room name: ");
        String name = readRequiredLine("Device name: ");
        double powerUsage = readDouble("Power usage (W): ");
        String range = readRequiredLine("Temperature range (min-max, e.g. 16-24): ");
        ThermostatDevice thermostat = new ThermostatDevice(name, powerUsage, range);
        int id = smartHomeService.addThermostatDevice(roomName, thermostat);
        System.out.println("Thermostat device added with id " + id + ".");
        askTargetTemperature(id, thermostat);
    }

    /**
     * Lets the user change the target of a thermostat that was just added. The device already
     * exists at this point, so an invalid target is asked again instead of aborting.
     */
    private void askTargetTemperature(int id, ThermostatDevice thermostat) throws DeviceNotFoundException {
        while (true) {
            String input = readLine("Target temperature (press Enter to keep "
                    + thermostat.getTargetTemperature() + "): ");
            if (input == null || input.isBlank()) {
                return;
            }
            try {
                smartHomeService.setTargetTemperature(id, parseDouble(input));
                System.out.println("Target temperature set to " + thermostat.getTargetTemperature() + ".");
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
    }

    private void removeDevice() {
        int id = readInt("Device id: ");
        if (smartHomeService.removeDevice(id)) {
            System.out.println("Device with id " + id + " removed.");
        } else {
            System.out.println("No device with id " + id + " found; nothing removed.");
        }
    }

    private void findDevicesWithPowerUsageLessThan() {
        double max = readDouble("Show devices using less than (W): ");
        List<Device> found = smartHomeService.findDevicesWithPowerUsageLessThan(max);
        if (found.isEmpty()) {
            System.out.println("No devices use less than " + formatWatts(max) + ".");
            return;
        }
        System.out.println(found.size() + " device(s) use less than " + formatWatts(max)
                + " (highest usage first):");
        smartHomeService.getDevicesSortedByPowerUsage().stream()
                .filter(found::contains)
                .forEach(this::printDevice);
    }

    private void showTotalPowerUsage() {
        System.out.println("Total power usage: " + formatWatts(smartHomeService.getTotalPowerUsage()));
    }

    private void showStats() {
        SmartHome.HomeStats stats = smartHomeService.getHomeStats();
        System.out.println("Average power usage: " + formatWatts(stats.getAveragePowerUsage()));
        Map<String, Integer> countByType = stats.getCountByType();
        if (countByType.isEmpty()) {
            System.out.println("Count by type: no devices yet.");
            return;
        }
        System.out.println("Count by type:");
        countByType.forEach((type, count) -> System.out.println("  " + type + ": " + count));
    }

    private void addRoom() {
        String name = readRequiredLine("Room name: ");
        smartHomeService.addRoom(name);
        System.out.println("Room '" + name.trim() + "' added.");
    }

    private void removeRoom() throws RoomNotFoundException, RoomNotEmptyException {
        String name = readRequiredLine("Room name: ");
        smartHomeService.removeRoom(name);
        System.out.println("Room '" + name.trim() + "' removed.");
    }

    // ---------- output helpers ----------

    private void printDevice(Device device) {
        System.out.println("  " + device.describe() + " power: " + formatWatts(device.getPowerUsage()));
    }

    private static String formatWatts(double watts) {
        // Locale.ROOT: always a decimal point, whatever the language of the computer.
        return String.format(Locale.ROOT, "%.2f W", watts);
    }

    // ---------- input helpers ----------

    /** Returns the next line, or {@code null} when there is no more input. */
    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    private String readRequiredLine(String prompt) {
        String line = readLine(prompt);
        if (line == null) {
            throw new InputEndedException();
        }
        return line;
    }

    private int readInt(String prompt) {
        String input = readRequiredLine(prompt).trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + input + "' is not a whole number.");
        }
    }

    private double readDouble(String prompt) {
        return parseDouble(readRequiredLine(prompt));
    }

    private static double parseDouble(String text) {
        String input = text.trim();
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + input + "' is not a number (use a decimal point, e.g. 12.5).");
        }
    }

    /** Signals that the input stream ended in the middle of an action. */
    private static class InputEndedException extends RuntimeException {
    }
}
