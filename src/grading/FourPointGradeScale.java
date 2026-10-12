package grading;

import exceptions.InvalidInputException;
import model.GradePoint;

/**
 * Converts percentage grades using a four-point GPA scale.
 */
public class FourPointGradeScale implements GradeScale {

    /**
     * Converts a percentage grade to a GradePoint object based on the 4.0 GPA scale.
     *
     * @param percentage the percentage grade to convert
     * @return a GradePoint object representing the corresponding GPA and letter grade
     * @throws InvalidInputException if the percentage is not between 0 and 100
     */
    @Override
    public GradePoint convertPercentageToGPA(double percentage) {
    if(percentage < 0 || percentage > 100) {
        throw new InvalidInputException("Percentage must be between 0 and 100");
    }
        if (percentage >= 93) {
            return new GradePoint(percentage, 4.0, "A");
        }
        if (percentage >= 90) {
            return new GradePoint(percentage, 3.7, "A-");
        }
        if (percentage >= 87) {
            return new GradePoint(percentage, 3.3, "B+");
        }
        if (percentage >= 83) {
            return new GradePoint(percentage, 3.0, "B");
        }
        if (percentage >= 80) {
            return new GradePoint(percentage, 2.7, "B-");
        }
        if (percentage >= 77) {
            return new GradePoint(percentage, 2.3, "C+");
        }
        if (percentage >= 73) {
            return new GradePoint(percentage, 2.0, "C");
        }
        if (percentage >= 70) {
            return new GradePoint(percentage, 1.7, "C-");
        }
        if (percentage >= 67) {
            return new GradePoint(percentage, 1.3, "D+");
        }
        if (percentage >= 60) {
            return new GradePoint(percentage, 1.0, "D");
        }

        return new GradePoint(percentage, 0.0, "F");
    }
}
