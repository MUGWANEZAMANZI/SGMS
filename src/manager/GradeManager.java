package manager;

import exceptions.InvalidGradeException;
import exceptions.CapacityExceededException;
import model.Grade;
import model.Student;
import model.Subject;

import java.util.Arrays;
import java.util.Objects;

/**
 * Stores grades and validates grade values.
 */
public class GradeManager implements Gradable {

    private final Grade[] grades = new Grade[200];
    private int gradeCount;

    /**
     * Adds a grade to the manager.
     *
     * @param grade grade to add
     * @throws CapacityExceededException if the grade capacity is reached
     * @throws NullPointerException if {@code grade} is {@code null}
     */
    public void addGrade(Grade grade) {
        if (gradeCount >= grades.length) {
            throw new CapacityExceededException("model.Grade capacity has been reached");
        }
        grades[gradeCount++] = Objects.requireNonNull(grade, "model.Grade cannot be null");
    }

    /**
     * Records a grade for a student and subject.
     *
     * @param student student receiving the grade
     * @param subject subject being graded
     * @param value percentage value
     * @throws InvalidGradeException if {@code value} is outside 0 through 100
     */
    public void recordGrade(Student student, Subject subject, double value) {
        if (!validateGrade(value)) {
            throw new InvalidGradeException(value);
        }
        Grade grade = new Grade(student.getStudentId(), subject, value);
        student.addGrade(grade);
        addGrade(grade);
    }

    /**
     * Validates a grade value.
     *
     * @param grade grade value to validate
     * @return whether the value is valid
     */
    @Override
    public boolean recordGrade(double grade) {
        return validateGrade(grade);
    }

    /**
     * Checks whether a grade is between 0 and 100 inclusive.
     *
     * @param grade grade value to validate
     * @return whether the value is valid
     */
    @Override
    public boolean validateGrade(double grade) {
        return grade >= 0 && grade <= 100;
    }

    /**
     * Returns grades belonging to a student.
     *
     * @param studentId student identifier
     * @return matching grades
     */
    public Grade[] getGradesByStudent(String studentId) {
        Grade[] result = new Grade[gradeCount];
        int resultCount = 0;
        for (int index = 0; index < gradeCount; index++) {
            if (grades[index].getStudentId().equalsIgnoreCase(studentId)) {
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
    public int getGradeCount() {
        return gradeCount;
    }

    /**
     * Returns all stored grades.
     *
     * @return a copy of the stored grades
     */
    public Grade[] getGrades() {
        return Arrays.copyOf(grades, gradeCount);
    }
}
