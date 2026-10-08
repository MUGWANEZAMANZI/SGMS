package report;

import manager.GradeManager;
import manager.StudentManager;

public class GradeReportService {
    private final StudentManager studentManager;
    private final GradeManager gradeManager;

    public GradeReportService(
            StudentManager studentManager,
            GradeManager gradeManager
    ) {
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
    }

    public GradeReport createReport(String studentId) {
        // Find student
        studentManager.findStudent(studentId);
        // Retrieve grades
        studentManager.getStudents();
        // Calculate averages
        gradeManager.
        // Create and return report.GradeReport
    }
}