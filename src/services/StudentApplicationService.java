package controller;

import contract.InputReader;
import manager.StudentManager;
import model.HonorsStudent;
import model.RegularStudent;
import model.Student;

public class StudentApplicationService {
    private final StudentManager studentManager;
    private final InputReader inputReader;

    public StudentApplicationService(StudentManager studentManager, InputReader inputReader) {
        this.studentManager = studentManager;
        this.inputReader = inputReader;
    }

    public void addStudent() {
        IO.println("\n============= ADD STUDENT =============");

        String name = inputReader.readName("Name: ");
        int age = inputReader.readInt("Age: ", 1, 120);
        double gpa = inputReader.readDouble("GPA: ", 0.0, 4.0);
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



    public static void pause() {
        IO.readln("\nPress Enter to continue...");
    }

}