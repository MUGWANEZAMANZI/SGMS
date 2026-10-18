package view;

import input.InputReader;
import manager.StudentManager;
import model.Student;
import report.GradeReport;
import report.GradeReportFileService;
import report.ImportedGradeReport;
import report.StudentImportService;
import services.BulkImportService;
import services.ConcurrentReportService;
import services.GradeService;
import services.StudentApplicationService;
import services.StudentSearchService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import logging.ApplicationLogger;
import exceptions.CapacityExceededException;
import exceptions.FileExportException;
import exceptions.InvalidInputException;
import exceptions.StudentNotFoundException;

/**
 * Presents the application's interactive main menu.
 */
public class Menu {

    private final Map<Integer, String> options = new LinkedHashMap<>();
    private final StudentApplicationService studentApplicationService;
    private final InputReader inputReader;
    private final BulkImportService bulkImportService;
    private final GradeService gradeService;
    private final StudentSearchService studentSearchService;
    private final StatisticsView statisticsView;
    private final GradeReportView gradeReportView;
    private final ExportReportView exportReportView;
    private final GPAView gpaView;
    private final StudentImportService studentImportService;
    private final ConcurrentReportService concurrentReportService;
    private final GradeReportFileService reportFileService;
    private final StudentManager studentManager;

    public Menu(
            StudentApplicationService studentApplicationService,
            GradeService gradeService,
            StudentSearchService studentSearchService,
            BulkImportService bulkImportService,
            InputReader inputReader,
            StatisticsView statisticsView,
            GradeReportView gradeReportView,
            ExportReportView exportReportView,
            GPAView gpaView,
            StudentImportService studentImportService,
            ConcurrentReportService concurrentReportService,
            GradeReportFileService reportFileService,
            StudentManager studentManager) {

        this.studentApplicationService = studentApplicationService;
        this.inputReader = inputReader;
        this.bulkImportService = bulkImportService;
        this.gradeService = gradeService;
        this.studentSearchService = studentSearchService;
        this.statisticsView = statisticsView;
        this.gradeReportView = gradeReportView;
        this.exportReportView = exportReportView;
        this.gpaView = gpaView;
        this.studentImportService = studentImportService;
        this.concurrentReportService = concurrentReportService;
        this.reportFileService = reportFileService;
        this.studentManager = studentManager;

        options.put(1, "Add Student");
        options.put(2, "View Students");
        options.put(3, "Record Grade");
        options.put(4, "View Grade Report");
        options.put(5, "Export Grade Report");
        options.put(6, "Calculate Student GPA");
        options.put(7, "Bulk Import Grades");
        options.put(8, "View Class Statistics");
        options.put(9, "Search Students");
        options.put(10, "Exit");
        options.put(11, "Import Students");
        options.put(12, "Generate Batch Reports");
        options.put(13, "Import Grade Report");
        options.put(14, "Edit Student");
        options.put(15, "Deactivate Student");
        options.put(16, "Activate Student");
        options.put(17, "Full Import Pipeline");
    }

    /** Starts the interactive menu loop. */
    public void start() {
        showHeader();

        boolean running = true;
        while (running) {
            showOptions();
            int option = inputReader.readInt("Choose an option: ", 1, 17);
            running = handleOptions(option);

            if (running) {
                inputReader.pause();
            }
        }
    }

    private void showHeader() {
        IO.println();
        IO.println("+--------------------------------------------+");
        IO.println("|      STUDENT GRADE MANAGEMENT SYSTEM       |");
        IO.println("+--------------------------------------------+");
    }

    private void showOptions() {
        IO.println();
        IO.println("---------------- MAIN MENU -----------------");
        options.forEach((number, label) -> IO.println("%d. %s".formatted(number, label)));
        IO.println("---------------------------------------------");
    }

    /**
     * Handles a selected menu option.
     *
     * @param option selected option number
     * @return {@code false} when the user selects exit; otherwise {@code true}
     */
    public boolean handleOptions(int option) {
        try {
            switch (option) {
                case 1 -> studentApplicationService.addStudent();
                case 2 -> studentApplicationService.viewStudents();
                case 3 -> gradeService.recordGrade();
                case 4 -> gradeReportView.display();
                case 5 -> exportReportView.export();
                case 6 -> gpaView.displayStudentGPA();
                case 7 -> {
                    String path = inputReader.readRequired("Enter grades CSV file path: ");
                    bulkImportService.importCSV(path);
                }
                case 8 -> statisticsView.displayStatistics();
                case 9 -> studentSearchService.search();
                case 11 -> {
                    if (studentImportService == null) {
                        IO.println("Student import is not configured.");
                        break;
                    }
                    String path = inputReader.readRequired(
                            "Enter student file path (.csv, .json, or .bin): ");
                    int imported = studentImportService.importStudents(Path.of(path));
                    IO.println(imported + " students imported successfully.");
                }
                case 12 -> {
                    Student[] students = studentManager.getStudents();
                    if (students.length == 0) {
                        IO.println("No students registered.");
                        break;
                    }
                    IO.println("Generating reports for " + students.length + " student(s) in background...");
                    List<GradeReport> reports = concurrentReportService.generate(students);
                    IO.println("\nBatch reports completed (" + reports.size() + " students):");
                    for (GradeReport report : reports) {
                        IO.println(report.generateTextReport());
                    }
                }
                case 13 -> {
                    String path = inputReader.readRequired(
                            "Enter report file path (.csv, .json, or .bin): ");
                    ImportedGradeReport imported = reportFileService.importReport(Path.of(path));
                    IO.println("\nImported report for: " + imported.studentName());
                    IO.println("Overall Average : %.2f%%".formatted(imported.overallAverage()));
                    IO.println("Core Average    : %.2f%%".formatted(imported.coreAverage()));
                    IO.println("Elective Average: %.2f%%".formatted(imported.electiveAverage()));
                    IO.println("Passing         : " + imported.passing());
                    if (!imported.grades().isEmpty()) {
                        IO.println("\nGrades:");
                        for (ImportedGradeReport.ImportedGrade g : imported.grades()) {
                            IO.println("  %-22s %.2f%% (%s)".formatted(
                                    g.subjectName(), g.value(), g.subjectType()));
                        }
                    }
                }
                case 14 -> studentApplicationService.editStudent();
                case 15 -> studentApplicationService.deactivateStudent();
                case 16 -> studentApplicationService.activateStudent();
                case 17 -> {
                    IO.println("\n========= FULL IMPORT PIPELINE =========");
                    if (studentImportService == null) {
                        IO.println("Student import is not configured.");
                        break;
                    }
                    String studentPath = inputReader.readRequired(
                            "Enter student file path (.csv, .json, or .bin): ");
                    int studentsImported = studentImportService.importStudents(Path.of(studentPath));
                    IO.println(studentsImported + " students imported.");
                    String gradesPath = inputReader.readRequired("Enter grades CSV file path: ");
                    bulkImportService.importCSV(gradesPath);
                }
                case 10 -> {
                    IO.println("\nThank you for using the Student Grade Management System!");
                    IO.println("Goodbye!");
                    return false;
                }
                default -> IO.println("Invalid option.");
            }
        } catch (StudentNotFoundException exception) {
            ApplicationLogger.warning("Student not found: " + exception.getMessage());
            IO.println("\nStudent not found: " + exception.getMessage());
        } catch (InvalidInputException
                 | CapacityExceededException
                 | FileExportException exception) {
            ApplicationLogger.error("Menu operation failed", exception);
            IO.println("\nInput error: " + exception.getMessage());
        } catch (IOException e) {
            ApplicationLogger.error("File operation failed", e);
            IO.println("\nCould not complete file operation: " + e.getMessage());
        }

        return true;
    }
}
