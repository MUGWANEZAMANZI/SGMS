package services;

import input.InputReader;
import manager.StudentManager;
import model.HonorsStudent;
import model.RegularStudent;
import model.Student;

import java.util.Objects;

/**
 * Provides interactive student-management operations.
 */
public class StudentApplicationService {
    private static final int REGULAR_STUDENT_SELECTION = 1;
    private static final int HONORS_STUDENT_SELECTION = 2;

    private final StudentManager studentManager;
    private final InputReader inputReader;

    /**
     * Creates a student application service.
     *
     * @param studentManager manager used to store students
     * @param inputReader reader used for interactive input
     */
    public StudentApplicationService(StudentManager studentManager, InputReader inputReader) {
        this.studentManager = Objects.requireNonNull(studentManager);
        this.inputReader = Objects.requireNonNull(inputReader);
    }

    /** Interactively creates and adds a student. */
    public void addStudent() {
        IO.println("\n============= ADD STUDENT =============");

        String name = inputReader.readName("Name: ");
        int age = inputReader.readInt("Age: ", 1, 120);
        String email = inputReader.readEmail("Email: ");
        String phone = inputReader.readRequired("Phone: ");

        IO.println("\nStudent type:");
        IO.println(REGULAR_STUDENT_SELECTION + ". Regular student");
        IO.println(HONORS_STUDENT_SELECTION + ". Honors student");

        int type = inputReader.readInt(
                "Select type: ",
                REGULAR_STUDENT_SELECTION,
                HONORS_STUDENT_SELECTION);

        Student student = type == REGULAR_STUDENT_SELECTION
                ? new RegularStudent(name, age, email, phone)
                : new HonorsStudent(name, age, email, phone);

        studentManager.addStudent(student);

        IO.println("Student added successfully!");
    }

    /** Displays all stored students and their summaries. */
    public void viewStudents() {
        IO.println("\n=============== STUDENTS ===============");

        Student[] students = studentManager.getStudents();
        if (students.length == 0) {
            IO.println("No students have been added yet.");
            return;
        }

        IO.println("%-8s %-20s %-10s %-10s %-10s".formatted("ID", "NAME", "TYPE", "AVERAGE", "STATUS"));
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


    private static String statusText(Student student) {
        if (student.getGrades().length == 0) {
            return "No grades";
        }
        return student.isPassing() ? "Passing" : "Failing";
    }
}