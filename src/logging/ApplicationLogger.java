package logging;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ApplicationLogger {
    private static final Logger LOGGER = Logger.getLogger("SGMS");

    private ApplicationLogger() {
    }

    public static void info(String message) {
        LOGGER.log(Level.INFO, "[{0}] {1}", new Object[]{Instant.now(), message});
    }

    public static void error(String message, Exception exception) {
        LOGGER.log(Level.SEVERE, "[{0}] {1}", new Object[]{Instant.now(), message});
        LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
    }
}
