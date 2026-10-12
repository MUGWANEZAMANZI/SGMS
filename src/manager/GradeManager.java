package manager;

import exceptions.InvalidGradeException;
import exceptions.CapacityExceededException;
import model.Grade;
import model.Student;
import model.Subject;

import java.util.Arrays;
import java.util.Objects;

public class GradeManager implements Gradable {

    private final Grade[] grades = new Grade[200];
    private int gradeCount;

    public void addGrade(Grade grade) {
        if (gradeCount >= grades.length) {
            throw new CapacityExceededException("model.Grade capacity has been reached");
        }
        grades[gradeCount++] = Objects.requireNonNull(grade, "model.Grade cannot be null");
    }

    public void recordGrade(Student student, Subject subject, double value) {
        if (!validateGrade(value)) {
            throw new InvalidGradeException(value);
        }
        Grade grade = new Grade(student.getStudentId(), subject, value);
        student.addGrade(grade);
        addGrade(grade);
    }

    @Override
    public boolean recordGrade(double grade) {
        return validateGrade(grade);
    }

    @Override
    public boolean validateGrade(double grade) {
        return grade >= 0 && grade <= 100;
    }

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

    public int getGradeCount() {
        return gradeCount;
    }

    public Grade[] getGrades() {
        return Arrays.copyOf(grades, gradeCount);
    }
}
