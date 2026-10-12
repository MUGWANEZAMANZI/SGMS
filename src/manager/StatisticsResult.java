package manager;

import model.Grade;
import model.Student;

import java.util.Collections;
import java.util.Map;

/**
 * Holds calculated class, grade, subject, and student statistics.
 */
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

    /**
     * Creates a statistics result from calculated values.
     *
     * @param students students included in the statistics
     * @param grades grades included in the statistics
     * @param mean mean grade
     * @param median median grade
     * @param mode mode grade
     * @param standardDeviation grade standard deviation
     * @param highestGradeRecord highest grade record
     * @param lowestGradeRecord lowest grade record
     * @param gradeCounts counts by letter grade
     * @param subjectAverages averages by subject
     * @param studentTypeAverages averages by student type
     */
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

    /** @return the number of students */
    public int getTotalStudents() {
        return totalStudents;
    }

    /** @return the number of grades */
    public int getTotalGrades() {
        return totalGrades;
    }

    /** @return the mean grade */
    public double getMean() {
        return mean;
    }

    /** @return the median grade */
    public double getMedian() {
        return median;
    }

    /** @return the mode grade */
    public double getMode() {
        return mode;
    }

    /** @return the grade standard deviation */
    public double getStandardDeviation() {
        return standardDeviation;
    }

    /** @return the highest grade value */
    public double getHighestGrade() {
        return highestGrade;
    }

    /** @return the lowest grade value */
    public double getLowestGrade() {
        return lowestGrade;
    }

    /** @return the record with the highest grade, or {@code null} when none exists */
    public Grade getHighestGradeRecord() {
        return highestGradeRecord;
    }

    /** @return the record with the lowest grade, or {@code null} when none exists */
    public Grade getLowestGradeRecord() {
        return lowestGradeRecord;
    }

    /**
     * Returns the count for a letter grade.
     *
     * @param letter letter grade to query
     * @return the count for the letter
     */
    public int getGradeCount(char letter) {
        return gradeCounts.getOrDefault(Character.toUpperCase(letter), 0);
    }

    /**
     * Returns subject averages.
     *
     * @return an unmodifiable map of subject averages
     */
    public Map<String, Double> getSubjectAverages() {
        return subjectAverages;
    }

    /**
     * Returns the average for a subject.
     *
     * @param subject subject name to query
     * @return the subject average, or {@code 0.0} when absent
     */
    public double getSubjectAverage(String subject) {
        return subjectAverages.getOrDefault(subject, 0.0);
    }

    /**
     * Returns the average for core subjects.
     *
     * @return the core-subject average
     */
    public double getCoreSubjectsAverage() {
        return averageForType("Core");
    }

    /**
     * Returns the average for elective subjects.
     *
     * @return the elective-subject average
     */
    public double getElectiveSubjectsAverage() {
        return averageForType("Elective");
    }

    /**
     * Returns averages grouped by student type.
     *
     * @return an unmodifiable map of student-type averages
     */
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
