package manager;

import model.Grade;
import model.Student;

import java.util.Collections;
import java.util.Map;

public class StatisticsResult {
    private final int totalStudents;
    private final int totalGrades;
    private final double mean;
    private final double median;
    private final double mode;
    private final double standardDeviation;
    private final double highestGrade;
    private final double lowestGrade;
    private final Grade highestGradeRecord;
    private final Grade lowestGradeRecord;
    private final Map<Character, Integer> gradeCounts;
    private final Map<String, Double> subjectAverages;
    private final Map<String, Double> studentTypeAverages;

    public StatisticsResult(
            Student[] students,
            Grade[] grades,
            double mean,
            double median,
            double mode,
            double standardDeviation,
            Grade highestGradeRecord,
            Grade lowestGradeRecord,
            Map<Character, Integer> gradeCounts,
            Map<String, Double> subjectAverages,
            Map<String, Double> studentTypeAverages) {
        this.totalStudents = students.length;
        this.totalGrades = grades.length;
        this.mean = mean;
        this.median = median;
        this.mode = mode;
        this.standardDeviation = standardDeviation;
        this.highestGradeRecord = highestGradeRecord;
        this.lowestGradeRecord = lowestGradeRecord;
        this.highestGrade = highestGradeRecord == null ? 0.0 : highestGradeRecord.getValue();
        this.lowestGrade = lowestGradeRecord == null ? 0.0 : lowestGradeRecord.getValue();
        this.gradeCounts = Collections.unmodifiableMap(gradeCounts);
        this.subjectAverages = Collections.unmodifiableMap(subjectAverages);
        this.studentTypeAverages = Collections.unmodifiableMap(studentTypeAverages);
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public int getTotalGrades() {
        return totalGrades;
    }

    public double getMean() {
        return mean;
    }

    public double getMedian() {
        return median;
    }

    public double getMode() {
        return mode;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public double getHighestGrade() {
        return highestGrade;
    }

    public double getLowestGrade() {
        return lowestGrade;
    }

    public Grade getHighestGradeRecord() {
        return highestGradeRecord;
    }

    public Grade getLowestGradeRecord() {
        return lowestGradeRecord;
    }

    public int getGradeCount(char letter) {
        return gradeCounts.getOrDefault(Character.toUpperCase(letter), 0);
    }

    public Map<String, Double> getSubjectAverages() {
        return subjectAverages;
    }

    public double getSubjectAverage(String subject) {
        return subjectAverages.getOrDefault(subject, 0.0);
    }

    public double getCoreSubjectsAverage() {
        return averageForType("Core");
    }

    public double getElectiveSubjectsAverage() {
        return averageForType("Elective");
    }

    public Map<String, Double> getStudentTypeAverages() {
        return studentTypeAverages;
    }

    private double averageForType(String type) {
        return subjectAverages.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(type + ":"))
                .mapToDouble(Map.Entry::getValue)
                .average()
                .orElse(0.0);
    }
}
