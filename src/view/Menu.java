package view;

import input.InputReader;
import services.BulkImportService;
import services.GradeService;
import services.StudentApplicationService;
import services.StudentSearchService;
import report.StudentImportService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import logging.ApplicationLogger;
import exceptions.CapacityExceededException;
import exceptions.FileExportException;
import exceptions.InvalidInputException;

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

    public Menu(
            StudentApplicationService studentApplicationService,
            GradeService gradeService,
            StudentSearchService studentSearchService,
            BulkImportService bulkImportService,
            InputReader inputReader,
            StatisticsView statisticsView,
            GradeReportView gradeReportView,
            ExportReportView exportReportView,
            GPAView gpaView) {
        this(studentApplicationService, gradeService, studentSearchService,
                bulkImportService, inputReader, statisticsView,
                gradeReportView, exportReportView, gpaView, null);
    }

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
            StudentImportService studentImportService) {

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
    }

    /** Starts the interactive menu loop. */
    public void start() {
        showHeader();

        boolean running = true;
        while (running) {
            showOptions();
            int option = inputReader.readInt("Choose an option: ", 1, 11);
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
                case 7 -> bulkImportService.importCSV("imports/grades_100.csv");
                case 8 -> statisticsView.displayStatistics();
                case 9 -> studentSearchService.search();
                case 11 -> {
                    if (studentImportService == null) {
                        IO.println("Student import is not configured.");
                        break;
                    }
                    String path = inputReader.readRequired(
                            "Enter student file path (.csv, .json, or .bin): ");
                    int imported = studentImportService.importStudents(
                            java.nio.file.Path.of(path));
                    IO.println(imported + " students imported successfully.");
                }
                case 10 -> {
                    IO.println("\nThank you for using the Student Grade Management System!");
                    IO.println("Goodbye!");
                    return false;
                }
                default -> IO.println("Invalid option.");
            }
        } catch (InvalidInputException
                 | CapacityExceededException
                 | FileExportException exception) {
            ApplicationLogger.error("Menu operation failed", exception);
            IO.println("\nInput error: " + exception.getMessage());
        } catch (IOException e) {
            ApplicationLogger.error("File operation failed", e);
            IO.println("\nCould not import CSV file:: " + e.getMessage());
        }

        return true;
    }
}
