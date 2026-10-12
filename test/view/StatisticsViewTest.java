package view;

import manager.GradeManager;
import manager.StatisticsCalculator;
import manager.StudentManager;
import model.CoreSubject;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StatisticsViewTest {

    @Test
    void displaysStatisticsAndGradeRecords() {
        StudentManager students = new StudentManager();
        GradeManager grades = new GradeManager();
        Student student = new RegularStudent(
                "Alice", 20, "alice@example.com", "0712345678");
        students.addStudent(student);
        grades.recordGrade(student, new CoreSubject("Mathematics", "CORE001"), 90);

        String output = captureOutput(() -> new StatisticsView(
                new StatisticsCalculator(students, grades)).displayStatistics());

        assertTrue(output.contains("CLASS STATISTICS"));
        assertTrue(output.contains("Mean: 90.00%"));
        assertTrue(output.contains("Highest: 90.00%"));
        assertTrue(output.contains("Lowest: 90.00%"));
    }

    private static String captureOutput(Runnable action) {
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
