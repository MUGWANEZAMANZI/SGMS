package grading;

import model.GradePoint;

/**
 * Converts percentage grades to grade-point representations.
 */
public interface GradeScale {
    /**
     * Converts a percentage to a GPA and letter grade.
     *
     * @param percentage percentage grade
     * @return the converted grade point
     */
    GradePoint convertPercentageToGPA(double percentage);
}
