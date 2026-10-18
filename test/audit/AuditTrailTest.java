package audit;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuditTrailTest {

    @Test
    void writesRecordedEventsAndCreatesParentDirectories() throws Exception {
        Path directory = Files.createTempDirectory("audit-test-");
        Path file = directory.resolve("nested").resolve("audit.log");

        try (AuditTrail auditTrail = new AuditTrail(file)) {
            auditTrail.record("IMPORT", "student=STU001");
            auditTrail.record("FAILURE", "invalid grade");
        }

        String content = Files.readString(file);
        assertTrue(content.contains("IMPORT | student=STU001"));
        assertTrue(content.contains("FAILURE | invalid grade"));
        assertEquals(2, content.lines().count());
    }

    @Test
    void rejectsNullAuditValues() throws Exception {
        Path file = Files.createTempFile("audit-test-", ".log");
        try (AuditTrail auditTrail = new AuditTrail(file)) {
            assertThrows(NullPointerException.class,
                    () -> auditTrail.record(null, "details"));
            assertThrows(NullPointerException.class,
                    () -> auditTrail.record("ACTION", null));
        }
        Files.deleteIfExists(file);
    }
}
