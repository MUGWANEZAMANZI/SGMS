import manager.GradeManager;
import manager.StudentManager;
import model.*;
import report.GradeReport;
import report.GradeReportService;
import report.TextGradeReportExporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
                case 5 -> exportGradeReport();
//                case 6 -> calculateStudentGPA();
//                case 7 -> bulkImportGrades();
//                case 8 -> viewClassStatistics();
//                case 9 -> searchStudents();
                case 10 -> {
                    IO.println("\nThank you for using model.Student model.Grade Management System!");
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

        IO.println("\nmodel.Student type:");
        IO.println("1. Regular student (passing grade: 50%)");
        IO.println("2. Honors student (passing grade: 60%)");
        int type = readInt("Select type: ", 1, 2);

        Student student = type == 1
                ? new RegularStudent(name, age, email, phone)
                : new HonorsStudent(name, age, email, phone);

        studentManager.addStudent(student);

        IO.println("\nmodel.Student added successfully!");
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

        IO.println("\nmodel.Student: %s - %s".formatted(student.getStudentId(), student.getName()));
        IO.println("Current average: %.2f%%".formatted(student.calculateAverageGrade()));

        int subjectTypeChoice = readInt(
                "\nmodel.Subject type (1. Core, 2. Elective): ", 1, 2);
        Subject subject = chooseSubject(subjectTypeChoice);
        double value = readDouble("model.Grade (0-100): ", 0, 100);
        Grade grade = new Grade(student.getStudentId(), subject, value);

        IO.println("\nmodel.Grade confirmation");
        IO.println("model.Grade ID: " + grade.getGradeId());
        IO.println("model.Student: " + student.getName());
        IO.println("model.Subject: %s (%s)".formatted(
                subject.getSubjectName(), subject.getSubjectType().toLowerCase()));
        IO.println("model.Grade: %.2f%%".formatted(grade.getValue()));

        String confirmation = readRequired("Save grade? (Y/N): ");
        if (confirmation.equalsIgnoreCase("Y")) {
            gradeManager.addGrade(grade);
            student.addGrade(grade);
            IO.println("model.Grade recorded successfully!");
        } else {
            IO.println("model.Grade discarded.");
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

        GradeReportService reportService = new GradeReportService(studentManager, gradeManager);
        String studentId = readRequired("Enter the student ID to view the grade report: ");
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

    private static Student findStudent() {
        if (studentManager.getStudentCount() == 0) {
            IO.println("No students have been added yet.");
            return null;
        }

        String id = readRequired("model.Student ID: ").toUpperCase();
        Student student = studentManager.findStudent(id);
        if (student == null) {
            IO.println("No student found with ID " + id + ".");
        }
        return student;
    }

    private static void printStudentSummary(Student student) {
        IO.println("model.Student ID: " + student.getStudentId());
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

    private static void exportGradeReport() {
        IO.println("\n========== EXPORT GRADE REPORT ==========");

        String studentId = readRequired("Enter the student ID to export: ");
        GradeReportService reportService = new GradeReportService(studentManager, gradeManager);
        GradeReport report = reportService.createReport(studentId);

        Path directory = Path.of("grade_reports");
        Path file = directory.resolve(studentId.toUpperCase() + "_report.txt");
        try {
            Files.createDirectories(directory);
            new TextGradeReportExporter().export(report, file);
            IO.println("Report exported to " + file.toAbsolutePath());
        } catch (IOException exception) {
            throw new IllegalStateException("Could not export report: " + exception.getMessage(), exception);
        }
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
                IO.println("The enter value is not valid. Please enter a valid number.");
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
