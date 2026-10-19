package report;

import exceptions.InvalidInputException;
import exceptions.StudentNotFoundException;
import manager.GradeManager;
import manager.StudentManager;
import model.CoreSubject;
import model.ElectiveSubject;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GradeReportServiceTest {

    private StudentManager studentManager;
    private GradeManager gradeManager;
    private GradeReportService service;

    @BeforeEach
    void setUp() {
        studentManager = new StudentManager();
        gradeManager = new GradeManager();
        service = new GradeReportService(studentManager, gradeManager);
    }

    @Test
    void rejectsNullStudentManager() {
        assertThrows(NullPointerException.class,
                () -> new GradeReportService(null, gradeManager));
    }

    @Test
    void rejectsNullGradeManager() {
        assertThrows(NullPointerException.class,
                () -> new GradeReportService(studentManager, null));
    }

    @Test
    void throwsOnBlankStudentId() {
        assertThrows(InvalidInputException.class, () -> service.createReport(""));
    }

    @Test
    void throwsOnNullStudentId() {
        assertThrows(InvalidInputException.class, () -> service.createReport(null));
    }

    @Test
    void throwsWhenStudentNotFound() {
        assertThrows(StudentNotFoundException.class,
                () -> service.createReport("STU999"));
    }

    @Test
    void createsReportWithCoreGrade() {
        Student student = new RegularStudent("Alice", 20, "alice@example.com", "0711111111");
        studentManager.addStudent(student);
        gradeManager.recordGrade(student, new CoreSubject("Math", "C001"), 90);

        GradeReport report = service.createReport(student.getStudentId());

        assertEquals("Alice", report.student().getName());
        assertEquals(90.0, report.coreAverage(), 0.001);
        assertEquals(0.0, report.electiveAverage(), 0.001);
        assertTrue(report.passing());
    }

    @Test
    void createsReportWithMixedGrades() {
        Student student = new RegularStudent("Dave", 21, "dave@example.com", "0722222222");
        studentManager.addStudent(student);
        gradeManager.recordGrade(student, new CoreSubject("Physics", "C002"), 60);
        gradeManager.recordGrade(student, new ElectiveSubject("Music", "E001"), 40);

        GradeReport report = service.createReport(student.getStudentId());

        assertEquals(60.0, report.coreAverage(), 0.001);
        assertEquals(40.0, report.electiveAverage(), 0.001);
    }

    @Test
    void reportHasZeroAveragesWhenNoGrades() {
        Student student = new RegularStudent("Eve", 19, "eve@example.com", "0733333333");
        studentManager.addStudent(student);

        GradeReport report = service.createReport(student.getStudentId());

        assertEquals(0.0, report.coreAverage(), 0.001);
        assertEquals(0.0, report.electiveAverage(), 0.001);
        assertEquals(0, report.grades().length);
    }
}
