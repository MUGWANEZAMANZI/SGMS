package services;

import input.InputReader;
import manager.StudentManager;
import model.HonorsStudent;
import model.RegularStudent;
import model.Student;

import java.util.Objects;

public class StudentApplicationService {
    private final StudentManager studentManager;
    private final InputReader inputReader;

    public StudentApplicationService(StudentManager studentManager, InputReader inputReader) {
        this.studentManager = Objects.requireNonNull(studentManager);
        this.inputReader = Objects.requireNonNull(inputReader);
    }

    public void addStudent() {
        IO.println("\n============= ADD STUDENT =============");

        String name = inputReader.readName("Name: ");
        int age = inputReader.readInt("Age: ", 1, 120);
        String email = inputReader.readEmail("Email: ");
        String phone = inputReader.readRequired("Phone: ");

        IO.println("\nStudent type:");
        IO.println("1. Regular student");
        IO.println("2. Honors student");

        int type = inputReader.readInt("Select type: ", 1, 2);

        Student student = type == 1
                ? new RegularStudent(name, age, email, phone)
                : new HonorsStudent(name, age, email, phone);

        studentManager.addStudent(student);

        IO.println("Student added successfully!");
    }

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