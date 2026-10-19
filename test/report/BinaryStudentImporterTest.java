package report;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BinaryStudentImporterTest {

    @TempDir
    Path tempDir;

    private final BinaryStudentImporter importer = new BinaryStudentImporter();

    private Path writeBinary(int count, String... students) throws IOException {
        Path file = tempDir.resolve("students.bin");
        try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(file))) {
            out.writeInt(count);
            int fieldsPerStudent = 6;
            for (int i = 0; i < students.length; i += fieldsPerStudent) {
                out.writeUTF(students[i]);
                out.writeInt(Integer.parseInt(students[i + 1]));
                out.writeUTF(students[i + 2]);
                out.writeUTF(students[i + 3]);
                out.writeUTF(students[i + 4]);
                out.writeUTF(students[i + 5]);
            }
        }
        return file;
    }

    @Test
    void parsesOneRecord() throws Exception {
        Path file = writeBinary(1,
                "Alice", "20", "alice@example.com", "0711111111", "regular", "ACTIVE");

        List<StudentRecord> records = importer.importStudents(file);

        assertEquals(1, records.size());
        StudentRecord r = records.get(0);
        assertEquals("Alice", r.name());
        assertEquals(20, r.age());
        assertEquals("alice@example.com", r.email());
        assertEquals("ACTIVE", r.status());
    }

    @Test
    void parsesTwoRecords() throws Exception {
        Path file = writeBinary(2,
                "Alice", "20", "alice@example.com", "0711111111", "regular", "ACTIVE",
                "Bob",   "21", "bob@example.com",   "0722222222", "honors",  "ACTIVE");

        List<StudentRecord> records = importer.importStudents(file);

        assertEquals(2, records.size());
        assertEquals("Bob", records.get(1).name());
    }

    @Test
    void parsesZeroRecords() throws Exception {
        Path file = writeBinary(0);

        List<StudentRecord> records = importer.importStudents(file);

        assertTrue(records.isEmpty());
    }

    @Test
    void throwsOnNegativeCount() throws Exception {
        Path file = tempDir.resolve("bad.bin");
        try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(file))) {
            out.writeInt(-1);
        }

        assertThrows(IOException.class, () -> importer.importStudents(file));
    }
}
