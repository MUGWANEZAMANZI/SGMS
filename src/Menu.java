import java.util.LinkedHashMap;
import java.util.Map;

public class Menu {

    private final Map<Integer, String> options = new LinkedHashMap<>();

    public Menu() {
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
            int option = Main.readInt("Choose an option: ", 1, 5);
            running = Main.operations(option);

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
}
