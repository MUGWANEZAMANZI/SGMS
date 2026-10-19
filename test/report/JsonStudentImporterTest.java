package report;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonStudentImporterTest {

    @TempDir
    Path tempDir;

    private final JsonStudentImporter importer = new JsonStudentImporter();

    private static final String SINGLE_STUDENT_JSON =
            "[\n" +
            "  {\"name\": \"Alice\", \"age\": 20, \"email\": \"alice@example.com\"," +
            "   \"phone\": \"0711111111\", \"type\": \"regular\", \"status\": \"ACTIVE\"}\n" +
            "]";

    @Test
    void parsesSingleStudentObject() throws Exception {
        Path file = tempDir.resolve("students.json");
        Files.writeString(file, SINGLE_STUDENT_JSON);

        List<StudentRecord> records = importer.importStudents(file);

        assertEquals(1, records.size());
        StudentRecord r = records.get(0);
        assertEquals("Alice", r.name());
        assertEquals(20, r.age());
        assertEquals("alice@example.com", r.email());
        assertEquals("0711111111", r.phone());
        assertEquals("regular", r.type());
        assertEquals("ACTIVE", r.status());
    }

    @Test
    void parsesMultipleObjects() throws Exception {
        Path file = tempDir.resolve("students.json");
        Files.writeString(file,
                "[" +
                "{\"name\": \"Alice\", \"age\": 20, \"email\": \"a@example.com\"," +
                " \"phone\": \"0700000001\", \"type\": \"regular\", \"status\": \"ACTIVE\"}," +
                "{\"name\": \"Bob\", \"age\": 21, \"email\": \"b@example.com\"," +
                " \"phone\": \"0700000002\", \"type\": \"honors\", \"status\": \"ACTIVE\"}" +
                "]");

        List<StudentRecord> records = importer.importStudents(file);

        assertEquals(2, records.size());
        assertEquals("Bob", records.get(1).name());
    }

    @Test
    void returnsEmptyListForEmptyArray() throws Exception {
        Path file = tempDir.resolve("empty.json");
        Files.writeString(file, "[]");

        List<StudentRecord> records = importer.importStudents(file);

        assertTrue(records.isEmpty());
    }

    @Test
    void throwsWhenRequiredFieldMissing() throws Exception {
        Path file = tempDir.resolve("bad.json");
        Files.writeString(file,
                "[{\"name\": \"Alice\", \"age\": 20, \"email\": \"a@x.com\"," +
                " \"phone\": \"0700000001\", \"type\": \"regular\"}]");

        assertThrows(IllegalArgumentException.class,
                () -> importer.importStudents(file));
    }
}
