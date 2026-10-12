package manager;

import model.Grade;
import model.Student;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Calculates statistics from managed students and grades.
 */
public class StatisticsCalculator {
    private final StudentManager studentManager;
    private final GradeManager gradeManager;

    /**
     * Creates a calculator backed by the supplied managers.
     *
     * @param studentManager source of students
     * @param gradeManager source of grades
     */
    public StatisticsCalculator(StudentManager studentManager, GradeManager gradeManager) {
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
    }

    /**
     * Calculates current class statistics.
     *
     * @return the calculated statistics
     */
    public StatisticsResult calculate() {
        Student[] students = studentManager.getStudents();
        Grade[] grades = gradeManager.getGrades();
        double[] values = Arrays.stream(grades).mapToDouble(Grade::getValue).toArray();
        double mean = average(values);

        Map<Character, Integer> gradeCounts = new LinkedHashMap<>();
        for (char letter : new char[]{'A', 'B', 'C', 'D', 'F'}) {
            gradeCounts.put(letter, 0);
        }

        Map<String, double[]> subjectTotals = new HashMap<>();
        Map<String, double[]> studentTypeTotals = new LinkedHashMap<>();
        Grade highest = null;
        Grade lowest = null;
        Map<Double, Integer> frequencies = new HashMap<>();

        for (Grade grade : grades) {
            highest = highest == null || grade.getValue() > highest.getValue() ? grade : highest;
            lowest = lowest == null || grade.getValue() < lowest.getValue() ? grade : lowest;
            char letter = gradeLetter(grade.getValue());
            gradeCounts.compute(letter, (key, count) -> count + 1);
            frequencies.merge(grade.getValue(), 1, Integer::sum);

            String subjectKey = grade.getSubject().getSubjectName();
            addTotal(subjectTotals, subjectKey, grade.getValue());
            addTotal(subjectTotals, grade.getSubject().getSubjectType() + ":" + subjectKey, grade.getValue());

            Student student = findStudent(students, grade.getStudentId());
            if (student != null) {
                addTotal(studentTypeTotals, student.getStudentType(), grade.getValue());
            }
        }

        Map<String, Double> subjectAverages = averages(subjectTotals);
        return new StatisticsResult(
                students,
                grades,
                mean,
                median(values),
                mode(frequencies),
                standardDeviation(values, mean),
                highest,
                lowest,
                gradeCounts,
                subjectAverages,
                averages(studentTypeTotals));
    }

    private static void addTotal(Map<String, double[]> totals, String key, double value) {
        double[] total = totals.computeIfAbsent(key, ignored -> new double[2]);
        total[0] += value;
        total[1]++;
    }

    private static Map<String, Double> averages(Map<String, double[]> totals) {
        Map<String, Double> result = new LinkedHashMap<>();
        totals.forEach((key, total) -> result.put(key, total[0] / total[1]));
        return result;
    }

    private static Student findStudent(Student[] students, String studentId) {
        for (Student student : students) {
            if (student.getStudentId().equalsIgnoreCase(studentId)) {
                return student;
            }
        }
        return null;
    }

    private static double average(double[] values) {
        return values.length == 0 ? 0.0 : Arrays.stream(values).average().orElse(0.0);
    }

    private static double median(double[] values) {
        if (values.length == 0) {
            return 0.0;
        }
        double[] sorted = values.clone();
        Arrays.sort(sorted);
        int middle = sorted.length / 2;
        return sorted.length % 2 == 0
                ? (sorted[middle - 1] + sorted[middle]) / 2.0
                : sorted[middle];
    }

    private static double mode(Map<Double, Integer> frequencies) {
        return frequencies.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0.0);
    }

    private static double standardDeviation(double[] values, double mean) {
        if (values.length == 0) {
            return 0.0;
        }
        double variance = Arrays.stream(values)
                .map(value -> Math.pow(value - mean, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }

    private static char gradeLetter(double value) {
        if (value >= 90) return 'A';
        if (value >= 80) return 'B';
        if (value >= 70) return 'C';
        if (value >= 60) return 'D';
        return 'F';
    }
}
