package grading;

import model.Grade;
import model.GradePoint;

import java.util.Objects;

/**
 * Calculates GPA values using a configurable grade scale.
 */
public final class GPACalculator implements GradeScale {
    private final GradeScale gradeScale;

    /**
     * Constructs a GPACalculator with the default FourPointGradeScale.
     * @return a new instance of GPACalculator using the FourPointGradeScale
     */
    public GPACalculator() {
        this(new FourPointGradeScale());
    }

    /**
     * Constructs a GPA calculator with the specified grade scale.
     *
     * @param gradeScale scale used for percentage conversion
     * @throws NullPointerException if {@code gradeScale} is {@code null}
     */
    public GPACalculator(GradeScale gradeScale) {
        this.gradeScale = Objects.requireNonNull(
                gradeScale, "Grade scale cannot be null");
    }

    /**
     * Converts a percentage grade using the configured scale.
     *
     * @param percentage percentage grade
     * @return the converted grade point
     */
    @Override
    public GradePoint convertPercentageToGPA(double percentage) {
        return gradeScale.convertPercentageToGPA(percentage);
    }

    /**
     * Calculates the average GPA for the supplied grades.
     *
     * @param grades grades to average
     * @return the cumulative GPA, or {@code 0.0} when no grades are supplied
     * @throws NullPointerException if {@code grades} is {@code null}
     */
    public double calculateCumulativeGPA(Grade[] grades) {
        Objects.requireNonNull(grades, "Grades cannot be null");
        if (grades.length == 0) {
            return 0.0;
        }

        double totalGpa = 0.0;
        for (Grade grade : grades) {
            totalGpa += convertPercentageToGPA(grade.getValue()).gpa();
        }
        return totalGpa / grades.length;
    }
}
