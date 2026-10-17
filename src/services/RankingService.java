package services;

import grading.GPACalculator;
import manager.GradeManager;
import model.Grade;
import model.Student;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Calculates student GPA data and creates mark- and GPA-based rankings.
 */
public final class RankingService {
    private final GradeManager gradeManager;
    private final GPACalculator gpaCalculator;

    /**
     * Creates a ranking service.
     *
     * @param gradeManager source of recorded grades
     * @param gpaCalculator calculator used for GPA conversion
     */
    public RankingService(GradeManager gradeManager, GPACalculator gpaCalculator) {
        this.gradeManager = Objects.requireNonNull(gradeManager, "Grade manager cannot be null");
        this.gpaCalculator = Objects.requireNonNull(gpaCalculator, "GPA calculator cannot be null");
    }

    /**
     * Calculates the subject and cumulative GPA values for a student.
     *
     * @param student student to calculate
     * @return calculated GPA data
     */
    public StudentGPAResult calculateStudentGPA(Student student) {
        Objects.requireNonNull(student, "Student cannot be null");
        Grade[] grades = gradeManager.getGradesByStudent(student.getStudentId());
        List<StudentGPAResult.SubjectGPAResult> subjectResults = new ArrayList<>();
        for (Grade grade : grades) {
            subjectResults.add(new StudentGPAResult.SubjectGPAResult(
                    grade.getSubject().getSubjectName(),
                    gpaCalculator.convertPercentageToGPA(grade.getValue())));
        }

        return new StudentGPAResult(
                student,
                student.calculateAverageGrade(),
                gpaCalculator.calculateCumulativeGPA(grades),
                subjectResults);
    }

    /**
     * Ranks students by average percentage mark, highest first.
     *
     * @param students students to rank
     * @return mark ranking
     */
    public List<StudentRanking> rankByMarks(Student[] students) {
        return rank(students, Comparator
                .comparingDouble(StudentRanking::averageMark)
                .reversed());
    }

    /**
     * Ranks students by cumulative GPA, highest first.
     *
     * @param students students to rank
     * @return GPA ranking
     */
    public List<StudentRanking> rankByGpa(Student[] students) {
        return rank(students, Comparator
                .comparingDouble(StudentRanking::cumulativeGpa)
                .reversed());
    }

    private List<StudentRanking> rank(
            Student[] students,
            Comparator<StudentRanking> comparator) {
        Objects.requireNonNull(students, "Students cannot be null");
        List<StudentRanking> rankings = Arrays.stream(students)
                .map(this::calculateStudentGPA)
                .map(result -> new StudentRanking(
                        0,
                        result.student(),
                        result.averageMark(),
                        result.cumulativeGpa()))
                .sorted(comparator.thenComparing(
                        ranking -> ranking.student().getStudentId()))
                .toList();

        List<StudentRanking> rankedResults = new ArrayList<>();
        for (int index = 0; index < rankings.size(); index++) {
            StudentRanking ranking = rankings.get(index);
            rankedResults.add(new StudentRanking(
                    index + 1,
                    ranking.student(),
                    ranking.averageMark(),
                    ranking.cumulativeGpa()));
        }
        return List.copyOf(rankedResults);
    }
}
