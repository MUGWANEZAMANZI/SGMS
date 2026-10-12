package report;

import manager.GradeManager;
import manager.StudentManager;
import model.CoreSubject;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GradeReportExportTest {

    @Test
    void exportsTextReportWithStudentSummary() throws Exception {
        StudentManager studentManager = new StudentManager();
        GradeManager gradeManager = new GradeManager();
        Student student = new RegularStudent(
                "Alice", 20, "alice@example.com", "0712345678");
        studentManager.addStudent(student);
        gradeManager.recordGrade(
                student, new CoreSubject("Mathematics", "CORE001"), 85);

        GradeReport report = new GradeReportService(
                studentManager, gradeManager).createReport(student.getStudentId());
        Path file = Files.createTempFile("grade-report-", ".txt");

        try {
            new TextGradeReportExporter().export(report, file);
            String content = Files.readString(file);

            assertTrue(content.contains("Student: Alice"));
            assertTrue(content.contains("Overall Average: 85.0"));
            assertTrue(content.contains("Passing: true"));
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
