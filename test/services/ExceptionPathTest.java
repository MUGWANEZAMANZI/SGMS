package services;

import grading.GPACalculator;
import exceptions.InvalidGradeException;
import exceptions.InvalidInputException;
import exceptions.StudentNotFoundException;
import manager.GradeManager;
import manager.StudentManager;
import model.CoreSubject;
import model.RegularStudent;
import org.junit.jupiter.api.Test;
import report.GradeReportService;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionPathTest {

    @Test
    void gradeManagerRejectsGradeOutsideRange() {
        GradeManager gradeManager = new GradeManager();
        RegularStudent student = new RegularStudent(
                "Alice", 20, "alice@example.com", "0712345678");

        assertThrows(InvalidGradeException.class, () ->
                gradeManager.recordGrade(
                        student, new CoreSubject("Mathematics", "CORE001"), 101));
    }

    @Test
    void gpaCalculatorRejectsNullGrades() {
        assertThrows(NullPointerException.class,
                () -> new GPACalculator().calculateCumulativeGPA(null));
    }

    @Test
    void reportServiceRejectsBlankStudentId() {
        GradeReportService service = new GradeReportService(
                new StudentManager(), new GradeManager());

        assertThrows(InvalidInputException.class, () -> service.createReport(" "));
    }

    @Test
    void reportServiceRejectsUnknownStudentId() {
        GradeReportService service = new GradeReportService(
                new StudentManager(), new GradeManager());

        assertThrows(StudentNotFoundException.class,
                () -> service.createReport("STU999"));
    }

    @Test
    void subjectRejectsBlankNameAndCode() {
        assertThrows(InvalidInputException.class,
                () -> new CoreSubject("", "CORE001"));
        assertThrows(InvalidInputException.class,
                () -> new CoreSubject("Mathematics", ""));
    }
}
