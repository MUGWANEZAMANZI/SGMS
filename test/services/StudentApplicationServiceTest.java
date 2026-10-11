package services;

import input.InputReader;
import manager.StudentManager;
import model.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class StudentApplicationServiceTest {

    @Test
    void regularStudentWithoutGradesIsNotPassing() {
        Student student = new RegularStudent(
                "Alice",
                20,
                "alice@example.com",
                "0712345678"
        );

        assertFalse(student.isPassing());
        assertEquals(0, student.getGrades().length);
    }

    @Test
    void regularStudentWithAverageBelow50IsFailing() {
        Student student = new RegularStudent(
                "Alice",
                20,
                "alice@example.com",
                "0712345678"
        );

        student.addGrade(new Grade(
                student.getStudentId(),
                new CoreSubject("Mathematics", "CORE001"),
                40
        ));

        assertFalse(student.isPassing());
    }

    @Test
    void regularStudentWithAverageOf50IsPassing() {
        Student student = new RegularStudent(
                "Alice",
                20,
                "alice@example.com",
                "0712345678"
        );

        student.addGrade(new Grade(
                student.getStudentId(),
                new CoreSubject("Mathematics", "CORE001"),
                50
        ));

        assertTrue(student.isPassing());
    }

    @Test
    void honorsStudentNeedsAverageOf60ToPass() {
        Student student = new HonorsStudent(
                "Bob",
                20,
                "bob@example.com",
                "0712345678"
        );

        student.addGrade(new Grade(
                student.getStudentId(),
                new CoreSubject("Mathematics", "CORE001"),
                59
        ));

        assertFalse(student.isPassing());

        student.addGrade(new Grade(
                student.getStudentId(),
                new CoreSubject("English", "CORE002"),
                61
        ));

        assertTrue(student.isPassing());
    }

    @Test
    void viewStudentsDisplaysNoGradesForStudentWithoutGrades() {
        StudentManager studentManager = new StudentManager();
        studentManager.addStudent(new RegularStudent(
                "Alice", 20, "alice@example.com", "0712345678"));

        String output = captureViewStudents(studentManager);

        assertTrue(output.contains("No grades"));
    }

    @Test
    void viewStudentsDisplaysPassingForStudentAtThreshold() {
        StudentManager studentManager = new StudentManager();
        RegularStudent student = new RegularStudent(
                "Alice", 20, "alice@example.com", "0712345678");
        student.addGrade(new Grade(
                student.getStudentId(),
                new CoreSubject("Mathematics", "CORE001"),
                50));
        studentManager.addStudent(student);

        String output = captureViewStudents(studentManager);

        assertTrue(output.contains("Passing"));
    }

    @Test
    void viewStudentsDisplaysFailingBelowThreshold() {
        StudentManager studentManager = new StudentManager();
        RegularStudent student = new RegularStudent(
                "Alice", 20, "alice@example.com", "0712345678");
        student.addGrade(new Grade(
                student.getStudentId(),
                new CoreSubject("Mathematics", "CORE001"),
                49));
        studentManager.addStudent(student);

        String output = captureViewStudents(studentManager);

        assertTrue(output.contains("Failing"));
    }

    private static String captureViewStudents(StudentManager studentManager) {
        StudentApplicationService service =
                new StudentApplicationService(studentManager, unusedInputReader());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            service.viewStudents();
            return output.toString();
        } finally {
            System.setOut(originalOutput);
        }
    }

    private static InputReader unusedInputReader() {
        return new InputReader() {
            @Override
            public int readInt(String prompt, int minimum, int maximum) {
                throw new UnsupportedOperationException();
            }

            @Override
            public double readDouble(String prompt, double minimum, double maximum) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String readName(String prompt) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String readEmail(String prompt) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String readRequired(String prompt) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void pause() {
                throw new UnsupportedOperationException();
            }
        };
    }
}