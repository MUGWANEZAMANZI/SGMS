package services;

import grading.GradeScale;
import input.InputReader;
import manager.GradeManager;
import manager.StudentManager;
import model.GradePoint;

import java.util.Objects;

public final class GPAService {
    private final GradeScale gradeScale;
    private final GradeManager gradeManager;
    private final StudentManager studentManager;
    private final InputReader inputReader;

    public GPAService(
            StudentManager studentManager,
            GradeManager gradeManager,
            GradeScale gradeScale,
            InputReader inputReader) {
        this.studentManager = Objects.requireNonNull(studentManager);
        this.gradeManager = Objects.requireNonNull(gradeManager);
        this.gradeScale = Objects.requireNonNull(gradeScale);
        this.inputReader = Objects.requireNonNull(inputReader);
    }

    public GradePoint getGPA(double percentage) {
        return gradeScale.convertPercentageToGPA(percentage);
    }

    public void calculateStudentGPA() {
        String studentId = inputReader.readRequired(
                "Enter the student ID to calculate GPA: ");
        var student = studentManager.findStudentById(studentId);
        if (student == null) {
            IO.println("No student found with ID " + studentId + ".");
            return;
        }

        var grades = gradeManager.getGradesByStudent(student.getStudentId());
        double total = 0.0;
        IO.println("\nSubject breakdown:");
        for (var grade : grades) {
            GradePoint point = getGPA(grade.getValue());
            total += point.gpa();
            IO.println("%-22s %.2f%% %.1f %s".formatted(
                    grade.getSubject().getSubjectName(),
                    point.percentage(),
                    point.gpa(),
                    point.letterGrade()));
        }

        double cumulative = grades.length == 0 ? 0.0 : total / grades.length;
        IO.println("Cumulative GPA: %.2f / 4.00".formatted(cumulative));
    }
}
