package app;

import input.InputReader;
import input.ConsoleInputReader;
import grading.GPACalculator;
import services.GradeService;
import services.BulkImportService;
import services.StudentApplicationService;
import services.StudentSearchService;
import manager.GradeManager;
import manager.StudentManager;
import manager.StatisticsCalculator;
import model.*;
import report.GradeReport;
import report.GradeReportService;
import report.TextGradeReportExporter;
import view.Menu;
import logging.ApplicationLogger;
import exceptions.FileExportException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Coordinates the application's services and menu actions.
 */
public class Main implements ApplicationActions {
    private final StudentManager studentManager;
    private final GradeManager gradeManager;
    private final InputReader inputReader;
    private final GradeService gradeService;
    private final StudentSearchService studentSearchService;
    private final GPACalculator gpaCalculator;
    private final GradeReportService reportService;

    /**
     * Creates an application coordinator with its required services.
     *
     * @param studentManager manages students
     * @param gradeManager manages grades
     * @param inputReader reads user input
     * @param gradeService handles grade operations
     * @param studentSearchService searches for students
     * @param gpaCalculator calculates GPA values
     * @param reportService creates grade reports
     */
    public Main(
            StudentManager studentManager,
            GradeManager gradeManager,
            InputReader inputReader,
            GradeService gradeService,
            StudentSearchService studentSearchService,
            GPACalculator gpaCalculator,
            GradeReportService reportService) {

        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
        this.inputReader = inputReader;
        this.gradeService = gradeService;
        this.studentSearchService = studentSearchService;
        this.gpaCalculator = gpaCalculator;
        this.reportService = reportService;
    }

    /**
     * The main entry point of the application.
     *
     * @param args command-line arguments (not used)
     */
    static void main(String[] args) {
        ApplicationLogger.info("Starting Student Grade Management System");
        createDefault().start();
    }

    private static Main createDefault() {
        StudentManager students = new StudentManager();
        GradeManager grades = new GradeManager();
        InputReader input = new ConsoleInputReader();
        GradeService gradeService = new GradeService(students, grades, input);
        StudentSearchService searchService = new StudentSearchService(students, input);
        return new Main(students, grades, input, gradeService, searchService,
                new GPACalculator(), new GradeReportService(students, grades));
    }

    private void start() {
        StudentApplicationService studentApplicationService =
                new StudentApplicationService(studentManager, inputReader);
        new Menu(
                studentApplicationService,
                gradeService,
                studentSearchService,
                new BulkImportService(studentManager, gradeManager),
                inputReader,
                new view.StatisticsView(new StatisticsCalculator(studentManager, gradeManager)),
                this).start();
    }

    /** Calculates and displays the selected student's GPA and class rank. */
    @Override
    public void calculateStudentGPA() {
        IO.println("\n============= CALCULATE GPA =============");
        String studentId = inputReader.readRequired(
                "Enter the student ID to calculate GPA: ").toUpperCase();
        Student student = studentManager.findStudentById(studentId);
        if (student == null) {
            IO.println("No student found with ID " + studentId + ".");
            return;
        }

        Grade[] grades = gradeManager.getGradesByStudent(student.getStudentId());
        double average = student.calculateAverageGrade();
        double cumulativeGpa = gpaCalculator.calculateCumulativeGPA(grades);

        IO.println("\nmodel.Student: %s - %s".formatted(student.getStudentId(), student.getName()));
        IO.println("Overall average: %.2f%%".formatted(average));
        IO.println("\n%-22s %-12s %-8s %-8s".formatted(
                "SUBJECT", "PERCENTAGE", "GPA", "LETTER"));
        IO.println("--------------------------------------------------------");
        for (Grade grade : grades) {
            GradePoint result = gpaCalculator.convertPercentageToGPA(
                    grade.getValue());
            IO.println("%-22s %9.2f%% %8.1f %-8s".formatted(
                    grade.getSubject().getSubjectName(),
                    result.percentage(),
                    result.gpa(),
                    result.letterGrade()));
        }
        IO.println("--------------------------------------------------------");
        IO.println("Cumulative GPA: %.2f / 4.00".formatted(cumulativeGpa));
        IO.println("Class rank: %d of %d".formatted(
                calculateClassRank(cumulativeGpa), studentManager.getStudentCount()));
        IO.println("Status: " + statusText(student));
    }


    /** Creates and displays the selected student's grade report. */
    @Override
    public void viewGradeReport() {
        IO.println("\n============= GRADE REPORT =============");

        String studentId = inputReader.readRequired(
                "Enter the student ID to view the grade report: ");
        GradeReport report = reportService.createReport(studentId);
        Student student = report.student();

        IO.println("\nmodel.Student: %s - %s".formatted(student.getStudentId(), student.getName()));
        IO.println("Type: " + student.getStudentType());
        IO.println("Passing grade: %.0f%%".formatted(student.getPassingGrade()));
        IO.println("Overall average: %.2f%%".formatted(report.overallAverage()));
        IO.println("Core average: %.2f%%".formatted(report.coreAverage()));
        IO.println("Elective average: %.2f%%".formatted(report.electiveAverage()));

        //Ternary operator to determine the status of the student based on the grades and passing criteria
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

    private Student findStudent() {
        if (studentManager.getStudentCount() == 0) {
            IO.println("No students have been added yet.");
            return null;
        }

        String id = inputReader.readRequired("model.Student ID: ").toUpperCase();
        Student student = studentManager.findStudentById(id);
        if (student == null) {
            IO.println("No student found with ID " + id + ".");
        }
        return student;
    }

    private void printStudentSummary(Student student) {
        IO.println("model.Student ID: " + student.getStudentId());
        IO.println("Name: " + student.getName());
        IO.println("Type: " + student.getStudentType());
        IO.println("Age: " + student.getAge());
        IO.println("Passing grade: %.0f%%".formatted(student.getPassingGrade()));
        IO.println("Status: " + student.getStatus());
    }



    /** Creates and exports the selected student's grade report. */
    @Override
    public void exportGradeReport() {
        IO.println("\n========== EXPORT GRADE REPORT ==========");

        String studentId = inputReader.readRequired(
                "Enter the student ID to export: ");
        GradeReport report = reportService.createReport(studentId);

        Path directory = Path.of("grade_reports");
        Path file = directory.resolve(studentId.toUpperCase() + "_report.txt");
        try {
            Files.createDirectories(directory);
            new TextGradeReportExporter().export(report, file);
            IO.println("Report exported to " + file.toAbsolutePath());
        } catch (IOException exception) {
            ApplicationLogger.error("Could not export grade report", exception);
            throw new FileExportException("Could not export report: "
                    + exception.getMessage(), exception);
        }
    }




    /** Pauses until the input reader receives confirmation. */
    @Override
    public void pause() {
        inputReader.pause();
    }

    private String statusText(Student student) {
        if (student.getGrades().length == 0) {
            return "No grades";
        }
        return student.isPassing() ? "Passing" : "Failing";
    }

    private int calculateClassRank(double studentGpa) {
        int rank = 1;
        for (Student student : studentManager.getStudents()) {
            double classGpa = gpaCalculator.calculateCumulativeGPA(
                    gradeManager.getGradesByStudent(student.getStudentId()));
            if (classGpa > studentGpa) {
                rank++;
            }
        }
        return rank;
    }
}
