package manager;

import exceptions.InvalidGradeException;
import exceptions.CapacityExceededException;
import model.Grade;
import model.Student;
import model.Subject;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Stores grades and validates grade values.
 */
public class GradeManager {

    private final Grade[] grades = new Grade[200];
    private int gradeCount;
    private final Set<String> uniqueCourses = new HashSet<>();

    /**
     * Adds a grade to the manager.
     *
     * @param grade grade to add
     * @throws CapacityExceededException if the grade capacity is reached
     * @throws NullPointerException if {@code grade} is {@code null}
     */
    public synchronized void addGrade(Grade grade) {
        if (gradeCount >= grades.length) {
            throw new CapacityExceededException("model.Grade capacity has been reached");
        }
        grades[gradeCount++] = Objects.requireNonNull(grade, "model.Grade cannot be null");
        uniqueCourses.add(grade.subject().getSubjectName());
    }

    /**
     * Returns the set of unique course names that have been graded.
     *
     * @return unmodifiable set of course names
     */
    public synchronized Set<String> getUniqueCourses() {
        return Collections.unmodifiableSet(uniqueCourses);
    }

    /**
     * Records a grade for a student and subject.
     *
     * @param student student receiving the grade
     * @param subject subject being graded
     * @param value percentage value
     * @throws InvalidGradeException if {@code value} is outside 0 through 100
     */
    public synchronized void recordGrade(Student student, Subject subject, double value) {
        if (!validateGrade(value)) {
            throw new InvalidGradeException(value);
        }
        Grade grade = new Grade(student.getStudentId(), subject, value);
        student.addGrade(grade);
        addGrade(grade);
    }

    private static boolean validateGrade(double grade) {
        return grade >= 0 && grade <= 100;
    }

    /**
     * Returns grades belonging to a student.
     *
     * @param studentId student identifier
     * @return matching grades
     */
    public synchronized Grade[] getGradesByStudent(String studentId) {
        Grade[] result = new Grade[gradeCount];
        int resultCount = 0;
        for (int index = 0; index < gradeCount; index++) {
            if (grades[index].studentId().equalsIgnoreCase(studentId)) {
                result[resultCount++] = grades[index];
            }
        }
        return Arrays.copyOf(result, resultCount);
    }

    /**
     * Returns the number of stored grades.
     *
     * @return the grade count
     */
    public synchronized int getGradeCount() {
        return gradeCount;
    }

    /**
     * Returns all stored grades.
     *
     * @return a copy of the stored grades
     */
    public synchronized Grade[] getGrades() {
        return Arrays.copyOf(grades, gradeCount);
    }
}
