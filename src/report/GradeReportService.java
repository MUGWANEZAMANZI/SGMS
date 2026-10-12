package report;

import exceptions.StudentNotFoundException;
import exceptions.InvalidInputException;
import manager.GradeManager;
import manager.StudentManager;
import model.CoreSubject;
import model.ElectiveSubject;
import model.Grade;
import model.Student;

import java.util.Objects;

/**
 * Creates grade reports from managed students and grades.
 */
public class GradeReportService {
    private final StudentManager studentManager;
    private final GradeManager gradeManager;

    /**
     * Creates a report service with the supplied managers.
     *
     * @param studentManager manager used to find students
     * @param gradeManager manager used to retrieve grades
     */
    public GradeReportService(StudentManager studentManager, GradeManager gradeManager) {
        this.studentManager = Objects.requireNonNull(studentManager, "Student manager cannot be null");
        this.gradeManager = Objects.requireNonNull(gradeManager, "Grade manager cannot be null");
    }

    /**
     * Creates a report for a student.
     *
     * @param studentId identifier of the student
     * @return the generated report
     * @throws InvalidInputException if {@code studentId} is blank
     * @throws StudentNotFoundException if no student has the identifier
     */
    public GradeReport createReport(String studentId) {
        if (studentId == null || studentId.isBlank()) {
            throw new InvalidInputException("Student ID cannot be blank");
        }

        Student student = studentManager.findStudentById(studentId);
        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }

        Grade[] grades = gradeManager.getGradesByStudent(student.getStudentId());
        double coreTotal = 0.0;
        int coreCount = 0;
        double electiveTotal = 0.0;
        int electiveCount = 0;

        for (Grade grade : grades) {
            if (grade.getSubject() instanceof CoreSubject) {
                coreTotal += grade.getValue();
                coreCount++;
            } else if (grade.getSubject() instanceof ElectiveSubject) {
                electiveTotal += grade.getValue();
                electiveCount++;
            }
        }

        return new GradeReport(
                student,
                grades,
                student.calculateAverageGrade(),
                average(coreTotal, coreCount),
                average(electiveTotal, electiveCount),
                student.isPassing()
        );
    }

    /**
     * Creates the report used for export.
     *
     * @param studentId identifier of the student
     * @return the generated report
     * @throws InvalidInputException if {@code studentId} is blank
     * @throws StudentNotFoundException if no student has the identifier
     */
    public GradeReport exportReport(String studentId) {
        return createReport(studentId);
    }

    private static double average(double total, int count) {
        return count == 0 ? 0.0 : total / count;
    }
}
