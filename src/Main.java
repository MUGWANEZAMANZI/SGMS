public class Main {

    private static final StudentManager studentManager = new StudentManager();
    private static final GradeManager gradeManager = new GradeManager();

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
        } catch (IllegalArgumentException | IllegalStateException exception) {
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

        studentManager.addStudent(student);

        IO.println("\nStudent added successfully!");
        printStudentSummary(student);
    }

    private static void viewStudents() {
        IO.println("\n=============== STUDENTS ===============");

        Student[] students = studentManager.getStudents();
        if (students.length == 0) {
            IO.println("No students have been added yet.");
            return;
        }

        IO.println("%-8s %-20s %-10s %-10s %-10s".formatted(
                "ID", "NAME", "TYPE", "AVERAGE", "STATUS"));
        IO.println("--------------------------------------------------------");

        double classTotal = 0;
        for (Student student : students) {
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
        IO.println("Total students: " + students.length);
        IO.println("Class average: %.2f%%".formatted(classTotal / students.length));
    }

    private static void recordGrade() {
        IO.println("\n============= RECORD GRADE =============");

        Student student = findStudent();
        if (student == null) {
            return;
        }

        IO.println("\nStudent: %s - %s".formatted(student.getStudentId(), student.getName()));
        IO.println("Current average: %.2f%%".formatted(student.calculateAverageGrade()));

        int subjectTypeChoice = readInt(
                "\nSubject type (1. Core, 2. Elective): ", 1, 2);
        Subject subject = chooseSubject(subjectTypeChoice);
        double value = readDouble("Grade (0-100): ", 0, 100);
        Grade grade = new Grade(student.getStudentId(), subject, value);

        IO.println("\nGrade confirmation");
        IO.println("Grade ID: " + grade.getGradeId());
        IO.println("Student: " + student.getName());
        IO.println("Subject: %s (%s)".formatted(
                subject.getSubjectName(), subject.getSubjectType().toLowerCase()));
        IO.println("Grade: %.2f%%".formatted(grade.getValue()));

        String confirmation = readRequired("Save grade? (Y/N): ");
        if (confirmation.equalsIgnoreCase("Y")) {
            gradeManager.addGrade(grade);
            student.addGrade(grade);
            IO.println("Grade recorded successfully!");
        } else {
            IO.println("Grade discarded.");
        }
    }

    private static Subject chooseSubject(int subjectTypeChoice) {
        String[] names;
        if (subjectTypeChoice == 1) {
            names = new String[]{"Mathematics", "English", "Science"};
        } else {
            names = new String[]{"Music", "Art", "Physical Education"};
        }

        IO.println("\nAvailable subjects:");
        for (int index = 0; index < names.length; index++) {
            IO.println("%d. %s".formatted(index + 1, names[index]));
        }

        int subjectChoice = readInt("Select subject: ", 1, names.length);
        String name = names[subjectChoice - 1];
        String code = subjectTypeChoice == 1
                ? "CORE%03d".formatted(subjectChoice)
                : "ELEC%03d".formatted(subjectChoice);

        return subjectTypeChoice == 1
                ? new CoreSubject(name, code)
                : new ElectiveSubject(name, code);
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

        Grade[] grades = gradeManager.getGradesByStudent(student.getStudentId());
        if (grades.length == 0) {
            IO.println("\nNo grades recorded for this student.");
            return;
        }

        IO.println("\nGRADE HISTORY");
        IO.println("%-8s %-22s %-10s %8s  %-12s".formatted(
                "ID", "SUBJECT", "TYPE", "GRADE", "DATE"));
        IO.println("--------------------------------------------------------");
        for (Grade grade : grades) {
            grade.displayGradeDetails();
        }
    }

    private static Student findStudent() {
        if (studentManager.getStudentCount() == 0) {
            IO.println("No students have been added yet.");
            return null;
        }

        String id = readRequired("Student ID: ").toUpperCase();
        Student student = studentManager.findStudent(id);
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
        if (student.getGrades().length == 0) {
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
                // Repeat the prompt with a clear message below.
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
                // Repeat the prompt with a clear message below.
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
