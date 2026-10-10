package grading;

import model.GradePoint;

public interface GradeScale {
    GradePoint convertPercentageToGPA(double percentage);
}
