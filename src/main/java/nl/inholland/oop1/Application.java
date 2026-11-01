package nl.inholland.oop1;

import nl.inholland.oop1.model.SmartHome;
import nl.inholland.oop1.service.SmartHomeService;
import nl.inholland.oop1.service.SmartHomeServiceImpl;
import nl.inholland.oop1.ui.ConsoleUserInterface;

/**
 * Entry point: creates and connects all objects and starts the console user interface.
 */
public class Application {

    private Application() {
        // Only the static main method is used.
    }

    /**
     * Starts the Smart Home application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SmartHome smartHome = new SmartHome();
        SmartHomeService smartHomeService = new SmartHomeServiceImpl(smartHome);
        ConsoleUserInterface userInterface = new ConsoleUserInterface(smartHomeService);
        userInterface.start();
    }
}
