package model;

import exceptions.InvalidGradeException;
import exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class GradeAndSubjectTest {

    @Test
    void createsAndDisplaysCoreAndElectiveSubjects() {
        CoreSubject core = new CoreSubject(" Mathematics ", " CORE001 ");
        ElectiveSubject elective = new ElectiveSubject("Art", "ELEC001");

        assertEquals("Mathematics", core.getSubjectName());
        assertEquals("CORE001", core.getSubjectCode());
        assertEquals("Core", core.getSubjectType());
        assertEquals("Elective", elective.getSubjectType());
    }

    @Test
    void rejectsBlankSubjectFieldsAndInvalidGrades() {
        assertAll(
                () -> assertThrows(InvalidInputException.class,
                        () -> new CoreSubject(" ", "C1")),
                () -> assertThrows(InvalidInputException.class,
                        () -> new ElectiveSubject("Art", "")),
                () -> assertThrows(InvalidInputException.class,
                        () -> new Grade("", new CoreSubject("Math", "C1"), 50)),
                () -> assertThrows(InvalidGradeException.class,
                        () -> new Grade("STU001",
                                new CoreSubject("Math", "C1"), -1)),
                () -> assertThrows(InvalidGradeException.class,
                        () -> new Grade("STU001",
                                new CoreSubject("Math", "C1"), 101))
        );
    }

    @Test
    void storesGradeDetailsAndFormatsRecordedDate() {
        Grade grade = new Grade("STU001",
                new CoreSubject("Math", "C1"), 75.5);

        assertEquals("STU001", grade.studentId());
        assertEquals(75.5, grade.value());
        assertEquals(grade.recordedAt().toString(), grade.formattedDate());

        // Capturing the output of displayGradeDetails for testing
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;

        try {
            System.setOut(new PrintStream(output));

            grade.displayGradeDetails();

            String text = output.toString();
            assertTrue(text.contains("Math"));
            assertTrue(text.contains("75.50%"));
            assertTrue(text.contains("Core"));
        } finally {
            System.setOut(original);
        }
    }



}
