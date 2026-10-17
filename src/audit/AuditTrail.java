package audit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Concurrent audit trail with a single background file writer.
 */
public final class AuditTrail implements AutoCloseable {
    private static final String STOP = "__AUDIT_STOP__";
    private final Path file;
    private final BlockingQueue<String> events = new LinkedBlockingQueue<>();
    private final Thread writer;

    public AuditTrail(Path file) {
        this.file = Objects.requireNonNull(file);
        this.writer = new Thread(this::writeEvents, "audit-trail-writer");
        this.writer.setDaemon(true);
        this.writer.start();
    }

    public void record(String action, String details) {
        Objects.requireNonNull(action);
        Objects.requireNonNull(details);
        events.add("%s | %s | %s%n".formatted(Instant.now(), action, details));
    }

    private void writeEvents() {
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            while (true) {
                String event = events.take();
                if (STOP.equals(event)) {
                    return;
                }
                Files.writeString(file, event, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } catch (IOException exception) {
            System.err.println("Audit trail write failed: " + exception.getMessage());
        }
    }

    @Override
    public void close() {
        events.add(STOP);
        try {
            writer.join(5000);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
