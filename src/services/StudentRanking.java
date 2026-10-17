package services;

import model.Student;

/**
 * Represents one student's position in a ranking.
 *
 * @param rank position in the ranking
 * @param student ranked student
 * @param averageMark student's average percentage mark
 * @param cumulativeGpa student's cumulative GPA
 */
public record StudentRanking(
        int rank,
        Student student,
        double averageMark,
        double cumulativeGpa) {
}
