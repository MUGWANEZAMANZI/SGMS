package report;

import manager.GradeManager;
import manager.StudentManager;
import model.CoreSubject;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GradeReportFileServiceTest {

    @TempDir
    Path tempDir;

    private GradeReportFileService service;
    private GradeReport report;

    @BeforeEach
    void setUp() {
        service = new GradeReportFileService();

        StudentManager sm = new StudentManager();
        GradeManager gm = new GradeManager();
        Student student = new RegularStudent("FileUser", 20, "file@example.com", "0700000088");
        sm.addStudent(student);
        gm.recordGrade(student, new CoreSubject("History", "C020"), 88);
        report = new GradeReportService(sm, gm).createReport(student.getStudentId());
    }

    @Test
    void exportsTxtByExtension() throws Exception {
        Path file = tempDir.resolve("report.txt");
        service.export(report, file);

        assertTrue(Files.exists(file), "TXT file must be created");
        assertTrue(Files.readString(file).contains("FileUser"));
    }

    @Test
    void exportsCsvByExtension() throws Exception {
        Path file = tempDir.resolve("report.csv");
        service.export(report, file);

        String content = Files.readString(file);
        assertTrue(content.contains("FileUser"));
    }

    @Test
    void exportsJsonByExtension() throws Exception {
        Path file = tempDir.resolve("report.json");
        service.export(report, file);

        String content = Files.readString(file);
        assertTrue(content.contains("\"student\""));
        assertTrue(content.contains("FileUser"));
    }

    @Test
    void exportsBinByExtension() throws Exception {
        Path file = tempDir.resolve("report.bin");
        service.export(report, file);

        assertTrue(Files.size(file) > 0, "Binary file must not be empty");
    }

    @Test
    void importsCsvRoundTrip() throws Exception {
        Path file = tempDir.resolve("report.csv");
        service.export(report, file);
        ImportedGradeReport imported = service.importReport(file);

        assertEquals("FileUser", imported.studentName());
        assertEquals(88.0, imported.overallAverage(), 0.001);
    }

    @Test
    void importsJsonRoundTrip() throws Exception {
        Path file = tempDir.resolve("report.json");
        service.export(report, file);
        ImportedGradeReport imported = service.importReport(file);

        assertEquals("FileUser", imported.studentName());
    }

    @Test
    void importsBinaryRoundTrip() throws Exception {
        Path file = tempDir.resolve("report.bin");
        service.export(report, file);
        ImportedGradeReport imported = service.importReport(file);

        assertEquals("FileUser", imported.studentName());
        assertTrue(imported.passing());
    }

    @Test
    void throwsOnUnsupportedExportExtension() {
        Path file = tempDir.resolve("report.xml");
        assertThrows(IllegalArgumentException.class,
                () -> service.export(report, file));
    }

    @Test
    void throwsOnUnsupportedImportExtension() {
        Path file = tempDir.resolve("report.txt");
        assertThrows(IllegalArgumentException.class,
                () -> service.importReport(file));
    }

    @Test
    void throwsOnFileWithNoExtension() {
        Path file = tempDir.resolve("report");
        assertThrows(IllegalArgumentException.class,
                () -> service.export(report, file));
    }

    @Test
    void throwsOnNullReport() {
        Path file = tempDir.resolve("report.txt");
        assertThrows(NullPointerException.class,
                () -> service.export(null, file));
    }
}
