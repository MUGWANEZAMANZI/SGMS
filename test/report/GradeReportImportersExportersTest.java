package report;

import manager.GradeManager;
import manager.StudentManager;
import model.CoreSubject;
import model.ElectiveSubject;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GradeReportImportersExportersTest {

    @TempDir
    Path tempDir;

    private GradeReport report;

    @BeforeEach
    void setUp() {
        StudentManager sm = new StudentManager();
        GradeManager gm = new GradeManager();
        Student student = new RegularStudent("TestUser", 21, "test@example.com", "0700000099");
        sm.addStudent(student);
        gm.recordGrade(student, new CoreSubject("Science", "C010"), 75);
        gm.recordGrade(student, new ElectiveSubject("Drama", "E010"), 65);
        report = new GradeReportService(sm, gm).createReport(student.getStudentId());
    }

    // ── CSV exporter / importer ───────────────────────────────────────────────

    @Test
    void csvExporterWritesReadableFile() throws Exception {
        Path file = tempDir.resolve("report.csv");
        new CsvGradeReportExporter().export(report, file);

        String content = Files.readString(file);
        assertTrue(content.contains("TestUser"), "CSV must contain student name");
        assertTrue(content.contains("75.0"), "CSV must contain overall average");
    }

    @Test
    void csvRoundTrip() throws Exception {
        Path file = tempDir.resolve("report.csv");
        new CsvGradeReportExporter().export(report, file);
        ImportedGradeReport imported = new CsvGradeReportImporter().importReport(file);

        assertEquals("TestUser", imported.studentName());
        assertEquals(70.0, imported.overallAverage(), 0.001);
        assertEquals(75.0, imported.coreAverage(), 0.001);
        assertEquals(65.0, imported.electiveAverage(), 0.001);
        assertTrue(imported.passing());
    }

    @Test
    void csvImporterThrowsOnEmptyFile() throws Exception {
        Path file = tempDir.resolve("empty.csv");
        Files.writeString(file, "Student,Overall Average,Core Average,Elective Average,Passing\n");

        assertThrows(IOException.class,
                () -> new CsvGradeReportImporter().importReport(file));
    }

    @Test
    void csvImporterThrowsOnTooFewColumns() throws Exception {
        Path file = tempDir.resolve("bad.csv");
        Files.writeString(file, "Student,Overall Average\nAlice,80\n");

        assertThrows(IOException.class,
                () -> new CsvGradeReportImporter().importReport(file));
    }

    // ── JSON exporter / importer ──────────────────────────────────────────────

    @Test
    void jsonExporterWritesReadableFile() throws Exception {
        Path file = tempDir.resolve("report.json");
        new JsonGradeReportExporter().export(report, file);

        String content = Files.readString(file);
        assertTrue(content.contains("\"student\""), "JSON must have student field");
        assertTrue(content.contains("TestUser"), "JSON must contain student name");
    }

    @Test
    void jsonRoundTrip() throws Exception {
        Path file = tempDir.resolve("report.json");
        new JsonGradeReportExporter().export(report, file);
        ImportedGradeReport imported = new JsonGradeReportImporter().importReport(file);

        assertEquals("TestUser", imported.studentName());
        assertEquals(75.0, imported.coreAverage(), 0.001);
        assertEquals(65.0, imported.electiveAverage(), 0.001);
        assertTrue(imported.passing());
    }

    @Test
    void jsonImporterThrowsOnMissingField() throws Exception {
        Path file = tempDir.resolve("bad.json");
        Files.writeString(file, "{\"student\": \"Alice\"}");

        assertThrows(IOException.class,
                () -> new JsonGradeReportImporter().importReport(file));
    }

    // ── Binary exporter / importer ────────────────────────────────────────────

    @Test
    void binaryRoundTripPreservesAllFields() throws Exception {
        Path file = tempDir.resolve("report.bin");
        new BinaryGradeReportExporter().export(report, file);
        ImportedGradeReport imported = new BinaryGradeReportImporter().importReport(file);

        assertEquals("TestUser", imported.studentName());
        assertEquals(70.0, imported.overallAverage(), 0.001);
        assertEquals(75.0, imported.coreAverage(), 0.001);
        assertEquals(65.0, imported.electiveAverage(), 0.001);
        assertTrue(imported.passing());
    }

    @Test
    void binaryRoundTripPreservesGradeRows() throws Exception {
        Path file = tempDir.resolve("report.bin");
        new BinaryGradeReportExporter().export(report, file);
        ImportedGradeReport imported = new BinaryGradeReportImporter().importReport(file);

        assertEquals(2, imported.grades().size());
        ImportedGradeReport.ImportedGrade first = imported.grades().get(0);
        assertEquals("C010", first.subjectCode());
        assertEquals(75.0, first.value(), 0.001);
    }
}
