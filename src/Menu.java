import controller.ConsoleInputReader;
import controller.StudentApplicationService;

import java.util.LinkedHashMap;
import java.util.Map;

public class Menu {

    private final Map<Integer, String> options = new LinkedHashMap<>();
    private final StudentApplicationService studentApplicationService;

    public Menu(StudentApplicationService studentApplicationService) {
        this.studentApplicationService = studentApplicationService;
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
            int option = ConsoleInputReader.readInt("Choose an option: ", 1, 10);
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
                case 2 -> Main.viewStudents();
                case 3 -> Main.recordGrade();
                case 4 -> Main.viewGradeReport();
                case 5 -> Main.exportGradeReport();
                case 6 -> Main.calculateStudentGPA();
                case 7 -> IO.println("Bulk import is not implemented yet.");
                case 8 -> IO.println("Class statistics are not implemented yet.");
                case 9 -> IO.println("Student search is not implemented yet.");
                case 10 -> {
                    IO.println("\nThank you for using the Student Grade Management System!");
                    IO.println("Goodbye!");
                    return false;
                }
                default -> IO.println("Invalid option.");
            }
        } catch (IllegalArgumentException | IllegalStateException exception) {
            IO.println("\nInput error: " + exception.getMessage());
        }

        return true;
    }
}
