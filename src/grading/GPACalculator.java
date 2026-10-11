package grading;

import model.Grade;
import model.GradePoint;

import java.util.Objects;

public final class GPACalculator implements GradeScale {
    private final GradeScale gradeScale;

    public GPACalculator() {
        this(new FourPointGradeScale());
    }

    public GPACalculator(GradeScale gradeScale) {
        this.gradeScale = Objects.requireNonNull(
                gradeScale, "Grade scale cannot be null");
    }

    @Override
    public GradePoint convertPercentageToGPA(double percentage) {
        return gradeScale.convertPercentageToGPA(percentage);
    }

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
