package services;

import model.GradePoint;
import model.Student;

import java.util.List;

/**
 * Contains the calculated GPA data for one student.
 *
 * @param student student represented by the result
 * @param averageMark average percentage mark
 * @param cumulativeGpa cumulative GPA
 * @param subjectResults GPA conversion for each subject grade
 */
public record StudentGPAResult(
        Student student,
        double averageMark,
        double cumulativeGpa,
        List<SubjectGPAResult> subjectResults) {

    public StudentGPAResult {
        subjectResults = List.copyOf(subjectResults);
    }

    /**
     * Contains the GPA conversion for one subject grade.
     *
     * @param subjectName subject name
     * @param gradePoint converted grade point
     */
    public record SubjectGPAResult(String subjectName, GradePoint gradePoint) {
    }
}
