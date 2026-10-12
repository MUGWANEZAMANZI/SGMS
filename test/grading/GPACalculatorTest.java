package grading;

import exceptions.InvalidInputException;
import model.CoreSubject;
import model.Grade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GPACalculatorTest {

    private final GPACalculator calculator = new GPACalculator();

    @Test
    void convertsPercentageToExpectedGradePoint() {
        assertEquals(4.0, calculator.convertPercentageToGPA(93).gpa());
        assertEquals("A", calculator.convertPercentageToGPA(93).letterGrade());
        assertEquals(2.7, calculator.convertPercentageToGPA(80).gpa());
        assertEquals("F", calculator.convertPercentageToGPA(59).letterGrade());
    }

    @Test
    void calculatesCumulativeGpaAcrossGrades() {
        Grade[] grades = {
                new Grade("STU001", new CoreSubject("Mathematics", "CORE001"), 93),
                new Grade("STU001", new CoreSubject("English", "CORE002"), 80)
        };

        assertEquals(3.35, calculator.calculateCumulativeGPA(grades), 0.0001);
    }

    @Test
    void returnsZeroForNoGrades() {
        assertEquals(0.0, calculator.calculateCumulativeGPA(new Grade[0]));
    }

    @Test
    void rejectsNullGrades() {
        assertThrows(NullPointerException.class,
                () -> calculator.calculateCumulativeGPA(null));
    }

    @Test
    void rejectsPercentageOutsideValidRange() {
        assertThrows(InvalidInputException.class,
                () -> calculator.convertPercentageToGPA(101));
    }

    @Test
    void acceptsPercentageAtBothRangeBoundaries() {
        assertEquals("F", calculator.convertPercentageToGPA(0).letterGrade());
        assertEquals(4.0, calculator.convertPercentageToGPA(100).gpa());
    }
}
