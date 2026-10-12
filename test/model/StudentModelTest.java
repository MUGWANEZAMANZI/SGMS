package model;

import exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentModelTest {

    @Test
    void validatesStudentFieldsAndUpdatesValues() {
        RegularStudent student = new RegularStudent(
                " Alice ", 20, " alice@example.com ", " 0712345678 ");

        assertEquals("Alice", student.getName());
        assertEquals("alice@example.com", student.getEmail());
        assertEquals("0712345678", student.getPhone());

        student.setName("Bob");
        student.setAge(21);
        student.setEmail("bob@example.com");
        student.setPhone("0799999999");
        student.setStatus(Student.Status.GRADUATED);

        assertEquals("Bob", student.getName());
        assertEquals(21, student.getAge());
        assertEquals(Student.Status.GRADUATED, student.getStatus());
    }

    @Test
    void rejectsBlankFieldsInvalidAgeAndNullStatus() {
        assertAll(
                () -> assertThrows(InvalidInputException.class,
                        () -> new RegularStudent("", 20, "a@b.com", "1")),
                () -> assertThrows(InvalidInputException.class,
                        () -> new RegularStudent("A", 0, "a@b.com", "1")),
                () -> assertThrows(InvalidInputException.class,
                        () -> new RegularStudent("A", 20, null, "1")),
                () -> assertThrows(NullPointerException.class, () -> {
                    RegularStudent student =
                            new RegularStudent("A", 20, "a@b.com", "1");
                    student.setStatus(null);
                })
        );
    }

    @Test
    void calculatesAverageAndHonorsEligibility() {
        HonorsStudent student = new HonorsStudent(
                "Honors", 20, "honors@example.com", "0712345678");
        assertFalse(student.checkHonorsEligibility());

        student.addGrade(new Grade(student.getStudentId(),
                new CoreSubject("Math", "C1"), 90));
        student.addGrade(new Grade(student.getStudentId(),
                new ElectiveSubject("Art", "E1"), 80));

        assertEquals(85.0, student.calculateAverageGrade());
        assertTrue(student.checkHonorsEligibility());
        assertTrue(student.isPassing());
    }

    @Test
    void rejectsNullAndMoreThanTwoHundredGrades() {
        RegularStudent student = new RegularStudent(
                "Student", 20, "student@example.com", "0712345678");
        assertThrows(NullPointerException.class, () -> student.addGrade(null));

        assertThrows(exceptions.CapacityExceededException.class, () ->
                {
                    for (int index = 0; index < 250; index++) {
                        student.addGrade(new Grade(student.getStudentId(),
                                new CoreSubject("Subject" + index, "C" + index), 50));
                    }
                });
    }
}
