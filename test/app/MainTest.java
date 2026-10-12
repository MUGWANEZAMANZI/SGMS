package app;

import grading.GPACalculator;
import input.InputReader;
import manager.GradeManager;
import manager.StudentManager;
import model.CoreSubject;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.Test;
import report.GradeReportService;
import services.GradeService;
import services.StudentSearchService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MainTest {

    @Test
    void calculatesGpaAndDisplaysClassRank() {
        StudentManager students = new StudentManager();
        GradeManager grades = new GradeManager();
        Student student = new RegularStudent(
                "Alice", 20, "alice@example.com", "0712345678");
        students.addStudent(student);
        grades.recordGrade(student, new CoreSubject("Mathematics", "CORE001"), 93);

        InputReader input = mock(InputReader.class);
        when(input.readRequired("Enter the student ID to calculate GPA: "))
                .thenReturn(student.getStudentId());
        Main main = new Main(students, grades, input,
                new GradeService(students, grades, input),
                new StudentSearchService(students, input),
                new GPACalculator(),
                new GradeReportService(students, grades));

        String output = captureOutput(main::calculateStudentGPA);

        assertTrue(output.contains("Cumulative GPA: 4.00 / 4.00"));
        assertTrue(output.contains("Class rank: 1 of 1"));
    }

    @Test
    void viewsEmptyGradeReport() {
        StudentManager students = new StudentManager();
        GradeManager grades = new GradeManager();
        Student student = new RegularStudent(
                "Alice", 20, "alice@example.com", "0712345678");
        students.addStudent(student);

        InputReader input = mock(InputReader.class);
        when(input.readRequired("Enter the student ID to view the grade report: "))
                .thenReturn(student.getStudentId());
        Main main = new Main(students, grades, input,
                new GradeService(students, grades, input),
                new StudentSearchService(students, input),
                new GPACalculator(),
                new GradeReportService(students, grades));

        assertTrue(captureOutput(main::viewGradeReport)
                .contains("No grades recorded for this student."));
    }

    private static String captureOutput(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output));
            action.run();
            return output.toString();
        } finally {
            System.setOut(original);
        }
    }
}
