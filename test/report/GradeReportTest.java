package report;

import manager.GradeManager;
import manager.StudentManager;
import model.CoreSubject;
import model.ElectiveSubject;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GradeReportTest {

    private GradeReport report;

    @BeforeEach
    void setUp() {
        StudentManager studentManager = new StudentManager();
        GradeManager gradeManager = new GradeManager();
        Student student = new RegularStudent("Bob", 22, "bob@example.com", "0700000001");
        studentManager.addStudent(student);
        gradeManager.recordGrade(student, new CoreSubject("Math", "C001"), 80);
        gradeManager.recordGrade(student, new ElectiveSubject("Art", "E001"), 70);
        report = new GradeReportService(studentManager, gradeManager)
                .createReport(student.getStudentId());
    }

    @Test
    void textReportContainsStudentName() {
        String text = report.generateTextReport();
        assertTrue(text.contains("Bob"), "Text report must include student name");
    }

    @Test
    void textReportContainsPassingStatus() {
        String text = report.generateTextReport();
        assertTrue(text.contains("Passing:"), "Text report must include passing status");
    }

    @Test
    void jsonReportIsValidJson() {
        String json = report.generateJsonReport();
        assertTrue(json.startsWith("{"), "JSON report must start with '{'");
        assertTrue(json.endsWith("}"), "JSON report must end with '}'");
    }

    @Test
    void jsonReportContainsStudentField() {
        String json = report.generateJsonReport();
        assertTrue(json.contains("\"student\""), "JSON must have student field");
        assertTrue(json.contains("Bob"), "JSON must include student name");
    }

    @Test
    void jsonReportContainsOverallAverage() {
        String json = report.generateJsonReport();
        assertTrue(json.contains("\"overallAverage\""), "JSON must have overallAverage field");
    }

    @Test
    void csvReportHasHeaderRow() {
        String csv = report.generateCsvReport();
        assertTrue(csv.startsWith("Student,Overall Average,Core Average,Elective Average,Passing"),
                "CSV must start with header row");
    }

    @Test
    void csvReportHasDataRow() {
        String csv = report.generateCsvReport();
        String[] lines = csv.split("\n");
        assertEquals(2, lines.length, "CSV must have header and one data row");
        assertTrue(lines[1].contains("Bob"), "CSV data row must include student name");
    }

    @Test
    void averagesAreComputedCorrectly() {
        assertEquals(80.0, report.coreAverage(), 0.001);
        assertEquals(70.0, report.electiveAverage(), 0.001);
    }
}
