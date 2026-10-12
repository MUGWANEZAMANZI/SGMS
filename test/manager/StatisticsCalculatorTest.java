package manager;

import model.CoreSubject;
import model.ElectiveSubject;
import model.Grade;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsCalculatorTest {

    @Test
    void calculatesClassStatisticsAndBreakdowns() {
        StudentManager studentManager = new StudentManager();
        GradeManager gradeManager = new GradeManager();
        Student regular = new RegularStudent(
                "Alice", 20, "alice@example.com", "0712345678");
        studentManager.addStudent(regular);

        gradeManager.recordGrade(
                regular, new CoreSubject("Mathematics", "CORE001"), 80);
        gradeManager.recordGrade(
                regular, new ElectiveSubject("Music", "ELEC001"), 90);
        gradeManager.recordGrade(
                regular, new CoreSubject("English", "CORE002"), 90);

        StatisticsResult result =
                new StatisticsCalculator(studentManager, gradeManager).calculate();

        assertEquals(1, result.getTotalStudents());
        assertEquals(3, result.getTotalGrades());
        assertEquals(86.6667, result.getMean(), 0.0001);
        assertEquals(90.0, result.getMedian());
        assertEquals(90.0, result.getMode());
        assertEquals(80.0, result.getLowestGrade());
        assertEquals(90.0, result.getHighestGrade());
        assertEquals(1, result.getGradeCount('B'));
        assertEquals(2, result.getGradeCount('A'));
        assertEquals(85.0, result.getCoreSubjectsAverage());
        assertEquals(90.0, result.getElectiveSubjectsAverage());
        assertEquals(86.6667, result.getStudentTypeAverages().get("Regular"), 0.0001);
    }

    @Test
    void returnsZeroStatisticsWhenThereAreNoGrades() {
        StatisticsResult result = new StatisticsCalculator(
                new StudentManager(), new GradeManager()).calculate();

        assertEquals(0, result.getTotalGrades());
        assertEquals(0.0, result.getMean());
        assertEquals(0.0, result.getMedian());
        assertEquals(0.0, result.getMode());
        assertEquals(0.0, result.getStandardDeviation());
        assertEquals(0.0, result.getHighestGrade());
        assertEquals(0.0, result.getLowestGrade());
    }
}
