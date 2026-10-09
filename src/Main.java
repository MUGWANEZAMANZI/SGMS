import contract.InputReader;
import controller.ConsoleInputReader;
import controller.StudentApplicationService;
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
        InputReader inputReader = new ConsoleInputReader();
        StudentApplicationService  studentApplicationService =
                new StudentApplicationService(studentManager, inputReader);
        new Menu(studentApplicationService).start();

    }



    static void viewStudents() {
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

    static void recordGrade() {
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
        double value = ConsoleInputReader.readDouble("model.Grade (0-100): ", 0, 100);
        Grade grade = new Grade(student.getStudentId(), subject, value);

        IO.println("\nmodel.Grade confirmation");
        IO.println("model.Grade ID: " + grade.getGradeId());
        IO.println("model.Student: " + student.getName());
        IO.println("model.Subject: %s (%s)".formatted(
                subject.getSubjectName(), subject.getSubjectType().toLowerCase()));
        IO.println("model.Grade: %.2f%%".formatted(grade.getValue()));

        String confirmation = ConsoleInputReader.readRequired("Save grade? (Y/N): ");
        if (confirmation.equalsIgnoreCase("Y")) {
            gradeManager.addGrade(grade);
            student.addGrade(grade);
            IO.println("model.Grade recorded successfully!");
        } else {
            IO.println("model.Grade discarded.");
        }
    }

    static void calculateStudentGPA() {
        IO.println("\n============= CALCULATE GPA =============");
        String studentId = inp.readRequired("Enter the student ID to calculate GPA: ");


        Student student = findStudent();
        if (student == null) {
            return;
        }

        double average = student.calculateAverageGrade();
        IO.println("\nmodel.Student: %s - %s".formatted(student.getStudentId(), student.getName()));
        IO.println("Current average: %.2f%%".formatted(average));
        IO.println("Status: " + statusText(student));
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

    static void viewGradeReport() {
        IO.println("\n============= GRADE REPORT =============");

        GradeReportService reportService = new GradeReportService(studentManager, gradeManager);
        String studentId = ConsoleInputReader.readRequired("Enter the student ID to view the grade report: ");
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

        String id = ConsoleInputReader.readRequired("model.Student ID: ").toUpperCase();
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

    static void exportGradeReport() {
        IO.println("\n========== EXPORT GRADE REPORT ==========");

        String studentId = ConsoleInputReader.readRequired("Enter the student ID to export: ");
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




    public static void pause() {
        IO.readln("\nPress Enter to continue...");
    }
}
