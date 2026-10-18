package view;

import model.HonorsStudent;
import model.Student;

/**
 * Displays details for a student.
 */
public final class StudentView {

    /**
     * Displays a student's identifier, details, average, status, and honors eligibility if applicable.
     *
     * @param student student to display
     */
    public void display(Student student) {
        IO.println("Student ID: " + student.getStudentId());
        IO.println("Name: " + student.getName());
        IO.println("Type: " + student.getStudentType());
        IO.println("Average: %.2f%%".formatted(student.calculateAverageGrade()));
        IO.println("Status: " + student.getStatus());
        if (student instanceof HonorsStudent h) {
            String eligibility = h.checkHonorsEligibility() ? "Maintained" : "NOT maintained";
            IO.println("Honors status: " + eligibility);
        }
    }
}
