package model;

/**
 * Contains a percentage grade, its GPA value, and its letter grade.
 *
 * @param percentage percentage grade
 * @param gpa GPA value
 * @param letterGrade letter-grade representation
 */
public record GradePoint(
        double percentage,
        double gpa,
        String letterGrade
) {
}
