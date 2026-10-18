package services;

import exceptions.InvalidInputException;
import exceptions.StudentNotFoundException;
import input.InputReader;
import manager.StudentManager;
import model.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentApplicationServiceTest {

    @Test
    void regularStudentWithoutGradesIsNotPassing() {
        Student student = new RegularStudent(
                "Alice", 20, "alice@example.com", "0712345678");
        assertFalse(student.isPassing());
        assertEquals(0, student.getGrades().length);
    }

    @Test
    void regularStudentWithAverageBelow50IsFailing() {
        Student student = new RegularStudent(
                "Alice", 20, "alice@example.com", "0712345678");
        student.addGrade(new Grade(student.getStudentId(), new CoreSubject("Mathematics", "CORE001"), 40));
        assertFalse(student.isPassing());
    }

    @Test
    void regularStudentWithAverageOf50IsPassing() {
        Student student = new RegularStudent(
                "Alice", 20, "alice@example.com", "0712345678");
        student.addGrade(new Grade(student.getStudentId(), new CoreSubject("Mathematics", "CORE001"), 50));
        assertTrue(student.isPassing());
    }

    @Test
    void honorsStudentNeedsAverageOf60ToPass() {
        Student student = new HonorsStudent(
                "Bob", 20, "bob@example.com", "0712345678");
        student.addGrade(new Grade(student.getStudentId(), new CoreSubject("Mathematics", "CORE001"), 59));
        assertFalse(student.isPassing());
        student.addGrade(new Grade(student.getStudentId(), new CoreSubject("English", "CORE002"), 61));
        assertTrue(student.isPassing());
    }

    // --- viewStudents now uses StudentView ---

    @Test
    void viewStudentsShowsStudentIdAndName() {
        StudentManager manager = new StudentManager();
        manager.addStudent(new RegularStudent("Alice", 20, "alice@example.com", "0712345678"));
        String output = captureViewStudents(manager);
        assertTrue(output.contains("Alice"), "Should contain student name");
        assertTrue(output.contains("Student ID"), "Should contain Student ID label from StudentView");
    }

    @Test
    void viewStudentsShowsNoStudentsMessage() {
        String output = captureViewStudents(new StudentManager());
        assertTrue(output.contains("No students have been added yet."));
    }

    @Test
    void viewStudentsShowsHonorsEligibilityForHonorsStudent() {
        StudentManager manager = new StudentManager();
        HonorsStudent student = new HonorsStudent("Bob", 20, "bob@example.com", "0712345678");
        student.addGrade(new Grade(student.getStudentId(), new CoreSubject("Math", "CORE001"), 90));
        manager.addStudent(student);
        String output = captureViewStudents(manager);
        assertTrue(output.contains("Honors status"), "StudentView should show honors eligibility line");
    }

    @Test
    void viewStudentsShowsMultipleStudents() {
        StudentManager manager = new StudentManager();
        manager.addStudent(new RegularStudent("Alice", 20, "alice@example.com", "0712345678"));
        manager.addStudent(new RegularStudent("Bob", 22, "bob@example.com", "0712345679"));
        String output = captureViewStudents(manager);
        assertTrue(output.contains("Alice"));
        assertTrue(output.contains("Bob"));
        assertTrue(output.contains("Total students: 2"));
    }

    // --- editStudent ---

    @Test
    void editStudentUpdatesNameWhenProvided() {
        StudentManager manager = new StudentManager();
        RegularStudent student = new RegularStudent("Alice", 20, "alice@example.com", "0712345678");
        manager.addStudent(student);

        InputReader reader = mock(InputReader.class);
        when(reader.readRequired("Student ID: ")).thenReturn(student.getStudentId());
        when(reader.readOptional(contains("Name"))).thenReturn("Alicia");
        when(reader.readOptional(contains("Age"))).thenReturn("");
        when(reader.readOptional(contains("Email"))).thenReturn("");
        when(reader.readOptional(contains("Phone"))).thenReturn("");

        new StudentApplicationService(manager, reader).editStudent();

        assertEquals("Alicia", student.getName());
    }

    @Test
    void editStudentKeepsValueWhenBlankInput() {
        StudentManager manager = new StudentManager();
        RegularStudent student = new RegularStudent("Alice", 20, "alice@example.com", "0712345678");
        manager.addStudent(student);

        InputReader reader = mock(InputReader.class);
        when(reader.readRequired("Student ID: ")).thenReturn(student.getStudentId());
        when(reader.readOptional(anyString())).thenReturn("");

        new StudentApplicationService(manager, reader).editStudent();

        assertEquals("Alice", student.getName());
        assertEquals(20, student.getAge());
        assertEquals("alice@example.com", student.getEmail());
    }

    @Test
    void editStudentUpdatesAgeWhenProvided() {
        StudentManager manager = new StudentManager();
        RegularStudent student = new RegularStudent("Alice", 20, "alice@example.com", "0712345678");
        manager.addStudent(student);

        InputReader reader = mock(InputReader.class);
        when(reader.readRequired("Student ID: ")).thenReturn(student.getStudentId());
        when(reader.readOptional(contains("Name"))).thenReturn("");
        when(reader.readOptional(contains("Age"))).thenReturn("25");
        when(reader.readOptional(contains("Email"))).thenReturn("");
        when(reader.readOptional(contains("Phone"))).thenReturn("");

        new StudentApplicationService(manager, reader).editStudent();

        assertEquals(25, student.getAge());
    }

    @Test
    void editStudentThrowsForNonNumericAge() {
        StudentManager manager = new StudentManager();
        RegularStudent student = new RegularStudent("Alice", 20, "alice@example.com", "0712345678");
        manager.addStudent(student);

        InputReader reader = mock(InputReader.class);
        when(reader.readRequired("Student ID: ")).thenReturn(student.getStudentId());
        when(reader.readOptional(contains("Name"))).thenReturn("");
        when(reader.readOptional(contains("Age"))).thenReturn("abc");

        assertThrows(InvalidInputException.class,
                () -> new StudentApplicationService(manager, reader).editStudent());
    }

    @Test
    void editStudentThrowsWhenNotFound() {
        InputReader reader = mock(InputReader.class);
        when(reader.readRequired("Student ID: ")).thenReturn("STU999");

        assertThrows(StudentNotFoundException.class,
                () -> new StudentApplicationService(new StudentManager(), reader).editStudent());
    }

    // --- deactivateStudent ---

    @Test
    void deactivateStudentSetsInactiveStatus() {
        StudentManager manager = new StudentManager();
        RegularStudent student = new RegularStudent("Alice", 20, "alice@example.com", "0712345678");
        manager.addStudent(student);

        InputReader reader = mock(InputReader.class);
        when(reader.readRequired("Student ID: ")).thenReturn(student.getStudentId());

        new StudentApplicationService(manager, reader).deactivateStudent();

        assertEquals(Student.Status.INACTIVE, student.getStatus());
    }

    @Test
    void deactivateStudentThrowsWhenNotFound() {
        InputReader reader = mock(InputReader.class);
        when(reader.readRequired("Student ID: ")).thenReturn("STU999");

        assertThrows(StudentNotFoundException.class,
                () -> new StudentApplicationService(new StudentManager(), reader).deactivateStudent());
    }

    // --- activateStudent ---

    @Test
    void activateStudentSetsActiveStatusAfterDeactivation() {
        StudentManager manager = new StudentManager();
        RegularStudent student = new RegularStudent("Alice", 20, "alice@example.com", "0712345678");
        student.setStatus(Student.Status.INACTIVE);
        manager.addStudent(student);

        InputReader reader = mock(InputReader.class);
        when(reader.readRequired("Student ID: ")).thenReturn(student.getStudentId());

        new StudentApplicationService(manager, reader).activateStudent();

        assertEquals(Student.Status.ACTIVE, student.getStatus());
    }

    @Test
    void activateStudentThrowsWhenNotFound() {
        InputReader reader = mock(InputReader.class);
        when(reader.readRequired("Student ID: ")).thenReturn("STU999");

        assertThrows(StudentNotFoundException.class,
                () -> new StudentApplicationService(new StudentManager(), reader).activateStudent());
    }

    // --- helpers ---

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
            @Override public int readInt(String p, int min, int max) { throw new UnsupportedOperationException(); }
            @Override public double readDouble(String p, double min, double max) { throw new UnsupportedOperationException(); }
            @Override public String readName(String p) { throw new UnsupportedOperationException(); }
            @Override public String readEmail(String p) { throw new UnsupportedOperationException(); }
            @Override public String readRequired(String p) { throw new UnsupportedOperationException(); }
            @Override public String readOptional(String p) { throw new UnsupportedOperationException(); }
            @Override public void pause() { throw new UnsupportedOperationException(); }
        };
    }
}
