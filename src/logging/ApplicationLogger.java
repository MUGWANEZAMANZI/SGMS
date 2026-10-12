package logging;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides application-level logging helpers.
 */
public final class ApplicationLogger {
    private static final Logger LOGGER = Logger.getLogger("SGMS");

    /**
     * Logs an informational message.
     *
     * @param message the message to log
     */
    public static void info(String message) {
        LOGGER.log(Level.INFO, "[{0}] {1}", new Object[]{Instant.now(), message});
    }

    /**
     * Logs an error message and its exception.
     *
     * @param message the error message
     * @param exception the exception associated with the error
     */
    public static void error(String message, Exception exception) {
        LOGGER.log(Level.SEVERE, "[{0}] {1}", new Object[]{Instant.now(), message});
        LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
    }
}
