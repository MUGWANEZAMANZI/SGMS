package view;

import model.CoreSubject;
import model.RegularStudent;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentViewTest {

    @Test
    void displaysStudentSummaryWithAverageAndStatus() {
        RegularStudent student = new RegularStudent(
                "Alice", 20, "alice@example.com", "0712345678");
        student.addGrade(new model.Grade(student.getStudentId(),
                new CoreSubject("Math", "C1"), 80));

        String output = capture(() -> new StudentView().display(student));

        assertTrue(output.contains("Alice"));
        assertTrue(output.contains("80.00%"));
        assertTrue(output.contains("ACTIVE"));
    }

    private static String capture(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output));
            action.run();
            return output.toString();
        } finally {
            System.setOut(original);
        }
    }
}
