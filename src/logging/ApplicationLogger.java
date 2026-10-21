package logging;

import java.io.IOException;
import java.time.Instant;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Provides application-level logging helpers.
 * All output goes to logs/app.log only — nothing is printed to the console,
 * so background threads never interrupt user input prompts.
 */
public final class ApplicationLogger {
    private static final Logger LOGGER = Logger.getLogger("SGMS");

    static {
        try {
            java.nio.file.Files.createDirectories(java.nio.file.Path.of("logs"));
            FileHandler fileHandler = new FileHandler("logs/app.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            // Remove every console handler from the root logger
            Logger root = Logger.getLogger("");
            for (Handler h : root.getHandlers()) {
                root.removeHandler(h);
            }
            LOGGER.addHandler(fileHandler);
            LOGGER.setUseParentHandlers(false);
        } catch (IOException e) {
            System.err.println("Could not configure file logging: " + e.getMessage());
        }
    }

    private ApplicationLogger() {}

    public static void info(String message) {
        LOGGER.log(Level.INFO, "[{0}] {1}", new Object[]{Instant.now(), message});
    }

    public static void warning(String message) {
        LOGGER.log(Level.WARNING, "[{0}] {1}", new Object[]{Instant.now(), message});
    }

    public static void error(String message, Exception exception) {
        LOGGER.log(Level.SEVERE, "[{0}] {1}", new Object[]{Instant.now(), message});
        LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
    }
}
