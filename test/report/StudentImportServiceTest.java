package report;

import manager.StudentManager;
import model.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.DataOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class StudentImportServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void importsCsvFile() throws Exception {
        Path file = tempDir.resolve("students.csv");
        Files.writeString(file,
                "name,age,email,phone,type,status\n" +
                "Alice,20,alice@example.com,0711111111,regular,ACTIVE\n");

        StudentManager manager = new StudentManager();
        int count = new StudentImportService(manager).importStudents(file);

        assertEquals(1, count);
        Student student = manager.getStudents()[0];
        assertEquals("Alice", student.getName());
    }

    @Test
    void importsJsonFile() throws Exception {
        Path file = tempDir.resolve("students.json");
        Files.writeString(file,
                "[{\"name\": \"Bob\", \"age\": 22, \"email\": \"bob@example.com\"," +
                " \"phone\": \"0722222222\", \"type\": \"honors\", \"status\": \"ACTIVE\"}]");

        StudentManager manager = new StudentManager();
        int count = new StudentImportService(manager).importStudents(file);

        assertEquals(1, count);
        assertEquals("Bob", manager.getStudents()[0].getName());
    }

    @Test
    void importsBinaryFile() throws Exception {
        Path file = tempDir.resolve("students.bin");
        try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(file))) {
            out.writeInt(1);
            out.writeUTF("Carol");
            out.writeInt(23);
            out.writeUTF("carol@example.com");
            out.writeUTF("0733333333");
            out.writeUTF("regular");
            out.writeUTF("ACTIVE");
        }

        StudentManager manager = new StudentManager();
        int count = new StudentImportService(manager).importStudents(file);

        assertEquals(1, count);
        assertEquals("Carol", manager.getStudents()[0].getName());
    }

    @Test
    void throwsOnUnsupportedExtension() {
        Path file = tempDir.resolve("students.xml");
        StudentImportService service = new StudentImportService(new StudentManager());

        assertThrows(IllegalArgumentException.class,
                () -> service.importStudents(file));
    }

    @Test
    void throwsOnNullFile() {
        StudentImportService service = new StudentImportService(new StudentManager());

        assertThrows(NullPointerException.class,
                () -> service.importStudents(null));
    }

    @Test
    void rejectsNullStudentManager() {
        assertThrows(NullPointerException.class,
                () -> new StudentImportService(null));
    }

    @Test
    void setsStudentStatusFromRecord() throws Exception {
        Path file = tempDir.resolve("students.csv");
        Files.writeString(file,
                "name,age,email,phone,type,status\n" +
                "Dan,25,dan@example.com,0744444444,regular,INACTIVE\n");

        StudentManager manager = new StudentManager();
        new StudentImportService(manager).importStudents(file);

        assertEquals(Student.Status.INACTIVE, manager.getStudents()[0].getStatus());
    }
}
