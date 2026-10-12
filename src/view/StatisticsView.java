package view;

import manager.StatisticsCalculator;
import manager.StatisticsResult;
import model.Grade;

import java.util.Map;

/**
 * Displays calculated class statistics.
 */
public class StatisticsView {
    private final StatisticsCalculator statisticsCalculator;

    /**
     * Creates a statistics view.
     *
     * @param statisticsCalculator calculator used to obtain statistics
     */
    public StatisticsView(StatisticsCalculator statisticsCalculator) {
        this.statisticsCalculator = statisticsCalculator;
    }

    /** Calculates and displays the current class statistics. */
    public void displayStatistics() {
        StatisticsResult statistics = statisticsCalculator.calculate();

        IO.println("\n============= CLASS STATISTICS =============");
        IO.println("Total Students: " + statistics.getTotalStudents());
        IO.println("Total Grades: " + statistics.getTotalGrades());
        IO.println("\nGRADE DISTRIBUTION");
        IO.println("---------------------------");
        for (char letter : new char[]{'A', 'B', 'C', 'D', 'F'}) {
            IO.println("%c: %d".formatted(letter, statistics.getGradeCount(letter)));
        }

        IO.println("\nSTATISTICAL ANALYSIS");
        IO.println("---------------------------");
        IO.println("Mean: %.2f%%".formatted(statistics.getMean()));
        IO.println("Median: %.2f%%".formatted(statistics.getMedian()));
        IO.println("Mode: %.2f%%".formatted(statistics.getMode()));
        IO.println("Standard Deviation: %.2f%%".formatted(statistics.getStandardDeviation()));
        IO.println("Range: %.2f%%".formatted(
                statistics.getHighestGrade() - statistics.getLowestGrade()));
        printGradeRecord("Highest", statistics.getHighestGradeRecord());
        printGradeRecord("Lowest", statistics.getLowestGradeRecord());

        IO.println("\nSUBJECT PERFORMANCE");
        IO.println("---------------------------");
        for (Map.Entry<String, Double> entry : statistics.getSubjectAverages().entrySet()) {
            if (!entry.getKey().contains(":")) {
                IO.println("%s: %.2f%%".formatted(entry.getKey(), entry.getValue()));
            }
        }
        IO.println("Core average: %.2f%%".formatted(statistics.getCoreSubjectsAverage()));
        IO.println("Elective average: %.2f%%".formatted(statistics.getElectiveSubjectsAverage()));

        IO.println("\nSTUDENT TYPE PERFORMANCE");
        IO.println("---------------------------");
        statistics.getStudentTypeAverages().forEach(
                (type, average) -> IO.println("%s: %.2f%%".formatted(type, average)));
    }

    private static void printGradeRecord(String label, Grade grade) {
        if (grade == null) {
            IO.println(label + ": N/A");
            return;
        }
        IO.println("%s: %.2f%% (%s, %s)".formatted(
                label,
                grade.getValue(),
                grade.getStudentId(),
                grade.getSubject().getSubjectName()));
    }
}
