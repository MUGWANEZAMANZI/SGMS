package model;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class GradeDisplayTest {

    @Test
    void gradeDisplayFormatContainsSubjectNameAndValue() {
        RegularStudent student = new RegularStudent("Alice", 20, "alice@example.com", "0712345678");
        CoreSubject subject = new CoreSubject("Chemistry", "CHEM001");
        Grade grade = new Grade(student.getStudentId(), subject, 92.5);

        String output = capture(grade::displayGradeDetails);

        assertTrue(output.contains("Chemistry"), "Output should contain subject name");
        assertTrue(output.contains("92.50"), "Output should contain grade value");
    }

    @Test
    void gradeDisplayFormatContainsSubjectType() {
        RegularStudent student = new RegularStudent("Alice", 20, "alice@example.com", "0712345678");
        ElectiveSubject subject = new ElectiveSubject("Art", "ART001");
        Grade grade = new Grade(student.getStudentId(), subject, 75.0);

        String output = capture(grade::displayGradeDetails);

        assertTrue(output.contains("Elective"), "Output should contain subject type");
    }

    @Test
    void gradeDisplayContainsGradeId() {
        RegularStudent student = new RegularStudent("Bob", 22, "bob@example.com", "0712345678");
        Grade grade = new Grade(student.getStudentId(), new CoreSubject("Physics", "PHY001"), 88.0);

        String output = capture(grade::displayGradeDetails);

        assertTrue(output.contains("GRD"), "Output should contain grade ID prefix");
    }

    @Test
    void gradeDisplayContainsRecordedDate() {
        RegularStudent student = new RegularStudent("Carol", 21, "carol@example.com", "0712345678");
        Grade grade = new Grade(student.getStudentId(), new CoreSubject("Biology", "BIO001"), 70.0);

        String output = capture(grade::displayGradeDetails);

        assertFalse(output.isBlank(), "Output should not be blank");
        assertTrue(output.contains("-"), "Formatted date should contain dashes");
    }

    private static String capture(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(buffer));
            action.run();
            return buffer.toString();
        } finally {
            System.setOut(original);
        }
    }
}
