package report;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvStudentImporterTest {

    @TempDir
    Path tempDir;

    private final CsvStudentImporter importer = new CsvStudentImporter();

    @Test
    void parsesValidCsvFile() throws Exception {
        Path file = tempDir.resolve("students.csv");
        Files.writeString(file,
                "name,age,email,phone,type,status\n" +
                "Alice,20,alice@example.com,0711111111,regular,ACTIVE\n");

        List<StudentRecord> records = importer.importStudents(file);

        assertEquals(1, records.size());
        StudentRecord record = records.get(0);
        assertEquals("Alice", record.name());
        assertEquals(20, record.age());
        assertEquals("alice@example.com", record.email());
        assertEquals("0711111111", record.phone());
        assertEquals("regular", record.type());
        assertEquals("ACTIVE", record.status());
    }

    @Test
    void parsesMultipleRows() throws Exception {
        Path file = tempDir.resolve("students.csv");
        Files.writeString(file,
                "name,age,email,phone,type,status\n" +
                "Alice,20,alice@example.com,0711111111,regular,ACTIVE\n" +
                "Bob,22,bob@example.com,0722222222,honors,ACTIVE\n");

        List<StudentRecord> records = importer.importStudents(file);

        assertEquals(2, records.size());
        assertEquals("Bob", records.get(1).name());
    }

    @Test
    void skipsBlankLines() throws Exception {
        Path file = tempDir.resolve("students.csv");
        Files.writeString(file,
                "name,age,email,phone,type,status\n" +
                "\n" +
                "Alice,20,alice@example.com,0711111111,regular,ACTIVE\n" +
                "\n");

        List<StudentRecord> records = importer.importStudents(file);

        assertEquals(1, records.size());
    }

    @Test
    void throwsOnWrongColumnCount() throws Exception {
        Path file = tempDir.resolve("bad.csv");
        Files.writeString(file,
                "name,age,email,phone,type,status\n" +
                "Alice,20,alice@example.com\n");

        assertThrows(IllegalArgumentException.class,
                () -> importer.importStudents(file));
    }

    @Test
    void skipsHeaderOnly() throws Exception {
        Path file = tempDir.resolve("empty.csv");
        Files.writeString(file, "name,age,email,phone,type,status\n");

        List<StudentRecord> records = importer.importStudents(file);

        assertTrue(records.isEmpty());
    }
}
