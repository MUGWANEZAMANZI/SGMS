import java.util.LinkedHashMap;
import java.util.Map;

public class Menu {

    private final Map<Integer, String> options = new LinkedHashMap<>();

    public Menu() {
        options.put(1, "Add Student");
        options.put(2, "View Students");
        options.put(3, "Record Grade");
        options.put(4, "View Grade Report");
        options.put(5, "Exit");
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
