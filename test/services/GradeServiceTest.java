package services;

import input.InputReader;
import manager.GradeManager;
import manager.StudentManager;
import model.CoreSubject;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

    @Mock
    private StudentManager studentManager;

    @Mock
    private GradeManager gradeManager;

    @Mock
    private InputReader inputReader;

    private GradeService service;

    @BeforeEach
    void setUp() {
        service = new GradeService(studentManager, gradeManager, inputReader);
    }

    @Test
    void chooseSubjectCreatesCoreSubjectFromInput() {
        when(inputReader.readInt("Select subject: ", 1, 3)).thenReturn(2);

        assertEquals("English", service.chooseSubject(1).getSubjectName());
        assertEquals("Core", service.chooseSubject(1).getSubjectType());
    }

    @Test
    void recordGradeDoesNotPersistWhenConfirmationIsNegative() {
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
        when(inputReader.readRequired("Save grade? (Y/N): ")).thenReturn("N");

        service.recordGrade();

        verify(gradeManager, never()).recordGrade(
                eq(student), any(CoreSubject.class), eq(85.0));
    }
}