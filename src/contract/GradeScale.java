package contract;

import model.GradePoint;

public interface GradeScale {
    GradePoint convertPercentageToGPA(double percentage);
}
