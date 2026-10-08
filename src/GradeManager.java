import java.util.Arrays;
import java.util.Objects;

public class GradeManager implements Gradable {

    private final Grade[] grades = new Grade[200];
    private int gradeCount;

    public void addGrade(Grade grade) {
        if (gradeCount >= grades.length) {
            throw new IllegalStateException("Grade capacity has been reached");
        }
        grades[gradeCount++] = Objects.requireNonNull(grade, "Grade cannot be null");
    }

    public void recordGrade(Student student, Subject subject, double value) {
        if (!validateGrade(value)) {
            throw new IllegalArgumentException("Grade must be between 0 and 100");
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
}
