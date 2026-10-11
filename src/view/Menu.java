package view;

import app.Main;
import input.InputReader;
import services.BulkImportService;
import services.GradeService;
import services.StudentApplicationService;
import services.StudentSearchService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Menu {

    private final Map<Integer, String> options = new LinkedHashMap<>();
    private final StudentApplicationService studentApplicationService;
    private final InputReader inputReader;
    private final BulkImportService bulkImportService;
    private final GradeService gradeService;
    private final StudentSearchService studentSearchService;

    public Menu(
            StudentApplicationService studentApplicationService,
            GradeService gradeService,
            StudentSearchService studentSearchService,
            BulkImportService bulkImportService,
            InputReader inputReader) {
        this.studentApplicationService = studentApplicationService;
        this.inputReader = inputReader;
        this.bulkImportService = bulkImportService;
        this.gradeService = gradeService;
        this.studentSearchService = studentSearchService;



        options.put(1, "Add model.Student");
        options.put(2, "View Students");
        options.put(3, "Record model.Grade");
        options.put(4, "View model.Grade Report");
        options.put(5, "Export model.Grade Report");
        options.put(6, "Calculate model.Student GPA");
        options.put(7, "Bulk Import Grades");
        options.put(8, "View Class Statistics");
        options.put(9, "Search Students");
        options.put(10, "Exit");
    }

    public void start() {
        showHeader();

        boolean running = true;
        while (running) {
            showOptions();
            int option = inputReader.readInt("Choose an option: ", 1, 10);
            running = handleOptions(option);

            if (running) {
                Main.pause();
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

    public boolean handleOptions(int option)
    {
        try {
            switch (option) {
                case 1 -> studentApplicationService.addStudent();
                case 2 -> studentApplicationService.viewStudents();
                case 3 -> gradeService.recordGrade();
                case 4 -> Main.viewGradeReport();
                case 5 -> Main.exportGradeReport();
                case 6 -> Main.calculateStudentGPA();
                case 7 -> bulkImportService.importCSV("path/to/grades.csv");
                case 8 -> IO.println("Class statistics are not implemented yet.");
                case 9 -> studentSearchService.search();
                case 10 -> {
                    IO.println("\nThank you for using the Student Grade Management System!");
                    IO.println("Goodbye!");
                    return false;
                }
                default -> IO.println("Invalid option.");
            }
        } catch (IllegalArgumentException | IllegalStateException exception) {
            IO.println("\nInput error: " + exception.getMessage());
        } catch (IOException e) {
            IO.println("\nCould not import CSV file:: " + e.getMessage());
        }

        return true;
    }
}
