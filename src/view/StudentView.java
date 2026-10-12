package view;

import model.Student;

/**
 * Displays details for a student.
 */
public final class StudentView {

    /**
     * Displays a student's identifier, details, average, and status.
     *
     * @param student student to display
     */
    public void display(Student student) {
        System.out.println("model.Student ID: " + student.getStudentId());
        System.out.println("Name: " + student.getName());
        System.out.println("Type: " + student.getStudentType());
        System.out.println("Average: %.2f%%".formatted(
                student.calculateAverageGrade()
        ));
        System.out.println("Status: " + student.getStatus());
    }
}