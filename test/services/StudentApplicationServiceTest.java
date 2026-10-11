package services;

import model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentPassingTest {

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
}