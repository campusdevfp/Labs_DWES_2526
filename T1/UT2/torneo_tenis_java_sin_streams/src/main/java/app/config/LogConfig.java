package app.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.*;

public class LogConfig {
    public static void setup() throws IOException {
        LogManager.getLogManager().reset();

        // Console handler (DEBUG -> FINE)
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.FINE);
        consoleHandler.setFormatter(new SimpleFormatter());
        Logger root = Logger.getLogger("");
        root.addHandler(consoleHandler);

        // File handler (INFO+)
        Files.createDirectories(Paths.get("logs"));
        FileHandler fileHandler = new FileHandler("logs/app.log", true);
        fileHandler.setLevel(Level.INFO);
        fileHandler.setFormatter(new SimpleFormatter());
        root.addHandler(fileHandler);

        root.setLevel(Level.FINE);
    }
}
