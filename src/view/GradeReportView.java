package view;

import input.InputReader;
import model.Grade;
import model.Student;
import report.GradeReport;
import report.GradeReportService;

public final class GradeReportView {
    private final InputReader inputReader;
    private final GradeReportService reportService;

    public GradeReportView(InputReader inputReader, GradeReportService reportService) {
        this.inputReader = inputReader;
        this.reportService = reportService;
    }

    public void display() {
        IO.println("\n============= GRADE REPORT =============");
        String studentId = inputReader.readRequired(
                "Enter the student ID to view the grade report: ");
        GradeReport report = reportService.createReport(studentId);
        Student student = report.student();

        IO.println("Type: " + student.getStudentType());
        IO.println("Passing grade: %.0f%%".formatted(student.getPassingGrade()));
        IO.println("Overall average: %.2f%%".formatted(report.overallAverage()));
        IO.println("Core average: %.2f%%".formatted(report.coreAverage()));
        IO.println("Elective average: %.2f%%".formatted(report.electiveAverage()));

        String status = report.grades().length == 0 ? "No grades" : report.passing() ? "Passing" : "Failing";
        IO.println("Status: " + status);

        Grade[] grades = report.grades();
        if (grades.length == 0) {
            IO.println("\nNo grades recorded for this student.");
            return;
        }

        IO.println("\n%-8s %-22s %-10s %-10s %-12s".formatted(
                "ID", "SUBJECT", "TYPE", "GRADE", "DATE"));
        IO.println("--------------------------------------------------------");
        for (Grade grade : grades) {
            grade.displayGradeDetails();
        }
    }
}
