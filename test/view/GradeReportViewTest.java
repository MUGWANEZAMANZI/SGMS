package view;

import input.InputReader;
import manager.GradeManager;
import manager.StudentManager;
import model.CoreSubject;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.Test;
import report.GradeReportService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GradeReportViewTest {

    @Test
    void displaysNoGradesMessageWhenStudentHasNoGrades() {
        StudentManager students = new StudentManager();
        GradeManager grades = new GradeManager();
        Student student = new RegularStudent(
                "Bob", 21, "bob@example.com", "0712345679");
        students.addStudent(student);

        InputReader input = mock(InputReader.class);
        when(input.readRequired("Enter the student ID to view the grade report: "))
                .thenReturn(student.getStudentId());
        GradeReportView view = new GradeReportView(input,
                new GradeReportService(students, grades));

        String output = captureOutput(view::display);

        assertTrue(output.contains("No grades recorded for this student."));
        assertTrue(output.contains("Bob"));
    }

    @Test
    void displaysGradeDetailsWhenStudentHasGrades() {
        StudentManager students = new StudentManager();
        GradeManager grades = new GradeManager();
        Student student = new RegularStudent(
                "Carol", 22, "carol@example.com", "0712345680");
        students.addStudent(student);
        grades.recordGrade(student, new CoreSubject("Physics", "CORE002"), 88);

        InputReader input = mock(InputReader.class);
        when(input.readRequired("Enter the student ID to view the grade report: "))
                .thenReturn(student.getStudentId());
        GradeReportView view = new GradeReportView(input,
                new GradeReportService(students, grades));

        String output = captureOutput(view::display);

        assertTrue(output.contains("Physics"));
        assertTrue(output.contains("Passing"));
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
