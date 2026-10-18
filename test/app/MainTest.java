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
import services.RankingService;
import view.GPAView;
import view.GradeReportView;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MainTest {

    @Test
    void gpaViewCalculatesGpaAndDisplaysClassRank() {
        StudentManager students = new StudentManager();
        GradeManager grades = new GradeManager();
        Student student = new RegularStudent(
                "Alice", 20, "alice@example.com", "0712345678");
        students.addStudent(student);
        grades.recordGrade(student, new CoreSubject("Mathematics", "CORE001"), 93);

        InputReader input = mock(InputReader.class);
        when(input.readRequired("Enter the student ID to calculate GPA: "))
                .thenReturn(student.getStudentId());
        GPAView gpaView = new GPAView(students, input,
                new RankingService(grades, new GPACalculator()));

        String output = captureOutput(gpaView::displayStudentGPA);

        assertTrue(output.contains("Cumulative GPA: 4.00 / 4.00"));
        assertTrue(output.contains("Class rank: 1 of 1"));
    }

    @Test
    void gradeReportViewShowsNoGradesMessage() {
        StudentManager students = new StudentManager();
        GradeManager grades = new GradeManager();
        Student student = new RegularStudent(
                "Alice", 20, "alice@example.com", "0712345678");
        students.addStudent(student);

        InputReader input = mock(InputReader.class);
        when(input.readRequired("Enter the student ID to view the grade report: "))
                .thenReturn(student.getStudentId());
        GradeReportView gradeReportView = new GradeReportView(input,
                new GradeReportService(students, grades));

        assertTrue(captureOutput(gradeReportView::display)
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
