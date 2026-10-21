package services;

import input.InputReader;
import manager.GradeManager;
import manager.Searchable;
import manager.StudentManager;
import model.CoreSubject;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MockitoServiceIntegrationTest {

    @Mock
    private Searchable searchable;

    @Mock
    private InputReader inputReader;

    @Mock
    private StudentManager studentManager;

    @Mock
    private GradeManager gradeManager;

    @Test
    void searchServiceUsesSearchDependencyAndDisplaysMatchingStudent() {
        Student student = new RegularStudent(
                "Alice", 20, "alice@example.com", "0712345678");
        when(inputReader.readInt("Select search option: ", 1, 5)).thenReturn(1);
        when(inputReader.readRequired("Enter student ID: ")).thenReturn("STU001");
        when(searchable.findStudentById("STU001")).thenReturn(student);

        String output = captureOutput(
                () -> new StudentSearchService(searchable, inputReader).search());

        verify(searchable).findStudentById("STU001");
        assertTrue(output.contains("Alice"));
        assertTrue(output.contains("1 found"));
    }

    @Test
    void studentApplicationServiceAddsTheSelectedStudent() {
        when(inputReader.readName("Name: ")).thenReturn("Alice");
        when(inputReader.readInt("Age: ", 1, 120)).thenReturn(20);
        when(inputReader.readEmail("Email: ")).thenReturn("alice@example.com");
        when(inputReader.readRequired("Phone: ")).thenReturn("0712345678");
        when(inputReader.readInt("Select type: ", 1, 2)).thenReturn(1);

        new StudentApplicationService(studentManager, inputReader).addStudent();

        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(studentManager).addStudent(captor.capture());
        assertEquals("Alice", captor.getValue().getName());
        assertEquals("Regular", captor.getValue().getStudentType());
    }

    @Test
    void gradeServiceRecordsConfirmedGradeThroughManager() {
        Student student = new RegularStudent(
                "Alice", 20, "alice@example.com", "0712345678");
        when(inputReader.readRequired("Enter the student ID to record a grade: "))
                .thenReturn("STU001");
        when(studentManager.findStudentById("STU001")).thenReturn(student);
        when(inputReader.readInt(
                "\nmodel.Subject type (1. Core, 2. Elective): ", 1, 2))
                .thenReturn(1);
        when(inputReader.readInt("Select subject: ", 1, 3)).thenReturn(1);
        when(inputReader.readDouble("model.Grade (0-100): ", 0, 100))
                .thenReturn(85.0);
        when(inputReader.readRequired("Save grade? (Y/N): ")).thenReturn("Y");

        new GradeService(studentManager, gradeManager, inputReader).recordGrade();

        verify(gradeManager).recordGrade(
                eq(student), any(CoreSubject.class), eq(85.0));
    }

    @Test
    void bulkImportUsesManagersAndSkipsUnknownStudent() throws Exception {
        Student student = new RegularStudent(
                "Alice", 20, "alice@example.com", "0712345678");
        when(studentManager.findStudentById("STU001")).thenReturn(student);
        when(studentManager.findStudentById("STU999")).thenReturn(null);

        Path csvFile = Files.createTempFile("grades-", ".csv");
        Files.writeString(csvFile,
                "studentId,subject,type,grade\n"
                        + "STU001,Mathematics,Core,85\n"
                        + "STU999,Mathematics,Core,90\n");

        try {
            new BulkImportService(studentManager, gradeManager)
                    .importCSV(csvFile.toString());

            verify(gradeManager).recordGrade(
                    eq(student), any(CoreSubject.class), eq(85.0));
        } finally {
            Files.deleteIfExists(csvFile);
        }
    }

    private static String captureOutput(Runnable action) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        try {
            System.setOut(new PrintStream(output));
            action.run();
            return output.toString();
        } finally {
            System.setOut(originalOutput);
        }
    }
}
