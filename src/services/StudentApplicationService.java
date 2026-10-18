package services;

import exceptions.InvalidInputException;
import exceptions.StudentNotFoundException;
import input.InputReader;
import manager.StudentManager;
import model.HonorsStudent;
import model.RegularStudent;
import model.Student;
import view.StudentView;

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

    /** Displays all stored students using StudentView. */
    public void viewStudents() {
        IO.println("\n=============== STUDENTS ===============");

        Student[] students = studentManager.getStudents();
        if (students.length == 0) {
            IO.println("No students have been added yet.");
            return;
        }

        StudentView view = new StudentView();
        for (Student student : students) {
            IO.println("----------------------------------------");
            view.display(student);
        }
        IO.println("----------------------------------------");
        IO.println("Total students: " + students.length);
    }

    /**
     * Interactively edits an existing student's details.
     * Fields left blank are kept unchanged.
     */
    public void editStudent() {
        IO.println("\n============= EDIT STUDENT =============");
        String studentId = inputReader.readRequired("Student ID: ");
        Student student = studentManager.findStudentById(studentId);
        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }
        IO.println("Editing: " + student.getName() + " (" + student.getStudentId() + ")");

        String name = inputReader.readOptional("Name [" + student.getName() + "]: ");
        if (!name.isBlank()) {
            student.setName(name);
        }

        String ageStr = inputReader.readOptional("Age [" + student.getAge() + "]: ");
        if (!ageStr.isBlank()) {
            try {
                student.setAge(Integer.parseInt(ageStr.trim()));
            } catch (NumberFormatException e) {
                throw new InvalidInputException("Age must be a number");
            }
        }

        String email = inputReader.readOptional("Email [" + student.getEmail() + "]: ");
        if (!email.isBlank()) {
            student.setEmail(email);
        }

        String phone = inputReader.readOptional("Phone [" + student.getPhone() + "]: ");
        if (!phone.isBlank()) {
            student.setPhone(phone);
        }

        IO.println("Student updated successfully!");
    }

    /** Interactively sets a student's status to INACTIVE. */
    public void deactivateStudent() {
        IO.println("\n============= DEACTIVATE STUDENT =============");
        String studentId = inputReader.readRequired("Student ID: ");
        Student student = studentManager.findStudentById(studentId);
        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }
        student.setStatus(Student.Status.INACTIVE);
        IO.println(student.getName() + " has been deactivated.");
    }

    /** Interactively sets a student's status to ACTIVE. */
    public void activateStudent() {
        IO.println("\n============= ACTIVATE STUDENT =============");
        String studentId = inputReader.readRequired("Student ID: ");
        Student student = studentManager.findStudentById(studentId);
        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }
        student.setStatus(Student.Status.ACTIVE);
        IO.println(student.getName() + " has been activated.");
    }
}
