package model;

import exceptions.InvalidGradeException;
import exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GradeAndSubjectTest {

    @Test
    void createsAndDisplaysCoreAndElectiveSubjects() {
        CoreSubject core = new CoreSubject(" Mathematics ", " CORE001 ");
        ElectiveSubject elective = new ElectiveSubject("Art", "ELEC001");

        assertEquals("Mathematics", core.getSubjectName());
        assertEquals("CORE001", core.getSubjectCode());
        assertTrue(core.isMandatory());
        assertFalse(elective.isMandatory());
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

        assertEquals("STU001", grade.getStudentId());
        assertEquals(75.5, grade.getValue());
        assertEquals(grade.getRecordedAt().toString(), grade.formattedDate());
        assertDoesNotThrow(grade::displayGradeDetails);
    }
}
