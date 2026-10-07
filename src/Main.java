import java.util.LinkedHashMap;
import java.util.Map;

public class Main {

    private static final Map<String, Student> students = new LinkedHashMap<>();

    public static void main(String[] args) {
        new Menu().start();
    }

    public static boolean operations(int option) {
        try {
            switch (option) {
                case 1 -> addStudent();
                case 2 -> viewStudents();
                case 3 -> recordGrade();
                case 4 -> viewGradeReport();
                case 5 -> {
                    IO.println("\nThank you for using Student Grade Management System!");
                    IO.println("Goodbye!");
                    return false;
                }
                default -> IO.println("Invalid option.");
            }
        } catch (IllegalArgumentException exception) {
            IO.println("\nInput error: " + exception.getMessage());
        }
        return true;
    }

    private static void addStudent() {
        IO.println("\n============= ADD STUDENT =============");

        String name = readRequired("Name: ");
        int age = readInt("Age: ", 1, 120);
        String email = readRequired("Email: ");
        String phone = readRequired("Phone: ");

        IO.println("\nStudent type:");
        IO.println("1. Regular student (passing grade: 50%)");
        IO.println("2. Honors student (passing grade: 60%)");
        int type = readInt("Select type: ", 1, 2);

        Student student = type == 1
                ? new RegularStudent(name, age, email, phone)
                : new HonorsStudent(name, age, email, phone);

        students.put(student.getStudentId(), student);

        IO.println("\n✓ Student added successfully!");
        printStudentSummary(student);
    }

    private static void viewStudents() {
        IO.println("\n=============== STUDENTS ===============");

        if (students.isEmpty()) {
            IO.println("No students have been added yet.");
            return;
        }

        IO.println("%-8s %-20s %-10s %-10s %-10s".formatted(
                "ID", "NAME", "TYPE", "AVERAGE", "STATUS"));
        IO.println("--------------------------------------------------------");

        double classTotal = 0;
        for (Student student : students.values()) {
            double average = student.calculateAverageGrade();
            classTotal += average;
            IO.println("%-8s %-20s %-10s %6.2f%%   %s".formatted(
                    student.getStudentId(),
                    student.getName(),
                    student.getStudentType(),
                    average,
                    statusText(student)));
        }

        IO.println("--------------------------------------------------------");
        IO.println("Total students: " + students.size());
        IO.println("Class average: %.2f%%".formatted(classTotal / students.size()));
    }

    private static void recordGrade() {
        IO.println("\n============= RECORD GRADE =============");

        Student student = findStudent();
        if (student == null) {
            return;
        }

        IO.println("\nStudent: %s - %s".formatted(student.getStudentId(), student.getName()));
        IO.println("Current average: %.2f%%".formatted(student.calculateAverageGrade()));

        IO.println("\nSubject type:");
        IO.println("1. Core (Mathematics, English, Science)");
        IO.println("2. Elective (Music, Art, Physical Education)");
        int subjectTypeChoice = readInt("Select type: ", 1, 2);

        SubjectType subjectType = subjectTypeChoice == 1 ? SubjectType.CORE : SubjectType.ELECTIVE;

        String[] subjects = subjectType == SubjectType.CORE
                ? new String[]{"Mathematics", "English", "Science"}
                : new String[]{"Music", "Art", "Physical Education"};

        IO.println("\nAvailable subjects:");
        for (int index = 0; index < subjects.length; index++) {
            IO.println("%d. %s".formatted(index + 1, subjects[index]));
        }

        int subjectChoice = readInt("Select subject: ", 1, subjects.length);
        double value = readDouble("Grade (0-100): ", 0, 100);
        Grade grade = new Grade(subjects[subjectChoice - 1], subjectType, value);

        IO.println("\nGrade confirmation");
        IO.println("Grade ID: " + grade.id());
        IO.println("Student: " + student.getName());
        IO.println("Subject: %s (%s)".formatted(grade.subject(), grade.subjectType().name().toLowerCase()));
        IO.println("Grade: %.2f%%".formatted(grade.value()));

        String confirmation = readRequired("Save grade? (Y/N): ");
        if (confirmation.equalsIgnoreCase("Y")) {
            student.addGrade(grade);
            IO.println("✓ Grade recorded successfully!");
        } else {
            IO.println("Grade discarded.");
        }
    }

    private static void viewGradeReport() {
        IO.println("\n============= GRADE REPORT =============");

        Student student = findStudent();
        if (student == null) {
            return;
        }

        IO.println("\nStudent: %s - %s".formatted(student.getStudentId(), student.getName()));
        IO.println("Type: " + student.getStudentType());
        IO.println("Passing grade: %.0f%%".formatted(student.getPassingGrade()));
        IO.println("Average: %.2f%%".formatted(student.calculateAverageGrade()));
        IO.println("Status: " + statusText(student));

        if (student.getGrades().isEmpty()) {
            IO.println("\nNo grades recorded for this student.");
            return;
        }

        IO.println("\nGRADE HISTORY");
        IO.println("%-8s %-22s %-10s %8s  %-12s".formatted(
                "ID", "SUBJECT", "TYPE", "GRADE", "DATE"));
        IO.println("--------------------------------------------------------");
        for (Grade grade : student.getGrades()) {
            IO.println("%-8s %-22s %-10s %7.2f%%  %-12s".formatted(
                    grade.id(),
                    grade.subject(),
                    grade.subjectType(),
                    grade.value(),
                    grade.formattedDate()));
        }
    }

    private static Student findStudent() {
        if (students.isEmpty()) {
            IO.println("No students have been added yet.");
            return null;
        }

        String id = readRequired("Student ID: ").toUpperCase();
        Student student = students.get(id);
        if (student == null) {
            IO.println("No student found with ID " + id + ".");
        }
        return student;
    }

    private static void printStudentSummary(Student student) {
        IO.println("Student ID: " + student.getStudentId());
        IO.println("Name: " + student.getName());
        IO.println("Type: " + student.getStudentType());
        IO.println("Age: " + student.getAge());
        IO.println("Passing grade: %.0f%%".formatted(student.getPassingGrade()));
        IO.println("Status: " + student.getStatus());
    }

    private static String statusText(Student student) {
        if (student.getGrades().isEmpty()) {
            return "No grades";
        }
        return student.isPassing() ? "Passing" : "Failing";
    }

    public static int readInt(String prompt, int minimum, int maximum) {
        while (true) {
            try {
                int value = Integer.parseInt(readRequired(prompt));
                if (value >= minimum && value <= maximum) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // The prompt is repeated with a clear message below.
            }
            IO.println("Enter a whole number from %d to %d.".formatted(minimum, maximum));
        }
    }

    private static double readDouble(String prompt, double minimum, double maximum) {
        while (true) {
            try {
                double value = Double.parseDouble(readRequired(prompt));
                if (value >= minimum && value <= maximum) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // The prompt is repeated with a clear message below.
            }
            IO.println("Enter a number from %.0f to %.0f.".formatted(minimum, maximum));
        }
    }

    private static String readRequired(String prompt) {
        while (true) {
            String value = IO.readln(prompt);
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
            IO.println("This value is required.");
        }
    }

    public static void pause() {
        IO.readln("\nPress Enter to continue...");
    }
}
