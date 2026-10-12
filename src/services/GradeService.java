package services;

import input.InputReader;
import manager.GradeManager;
import manager.StudentManager;
import model.*;

import java.util.Objects;

/**
 * Provides interactive grade-recording operations.
 */
public class GradeService {
    private final StudentManager studentManager;
    private final GradeManager gradeManager;
    private final InputReader inputReader;

    /**
     * Creates a grade service.
     *
     * @param studentManager manager used to find students
     * @param gradeManager manager used to record grades
     * @param inputReader reader used for interactive input
     */
    public GradeService(StudentManager studentManager, GradeManager gradeManager, InputReader inputReader
    ) {
        this.studentManager = Objects.requireNonNull(studentManager);
        this.gradeManager = Objects.requireNonNull(gradeManager);
        this.inputReader = Objects.requireNonNull(inputReader);
    }

    /** Interactively records a grade for a student. */
     public void recordGrade() {
        IO.println("\n============= RECORD GRADE =============");
        String studentId = inputReader.readRequired("Enter the student ID to record a grade: ");

        Student student = studentManager.findStudentById(studentId);
        if (student == null) {
            IO.println("No student found with ID " + studentId + ".");
            return;
        }

        IO.println("\nmodel.Student: %s - %s".formatted(student.getStudentId(), student.getName()));
        IO.println("Current average: %.2f%%".formatted(student.calculateAverageGrade()));

        int subjectTypeChoice = inputReader.readInt(
                "\nmodel.Subject type (1. Core, 2. Elective): ", 1, 2);
        Subject subject = chooseSubject(subjectTypeChoice);
        double value = inputReader.readDouble("model.Grade (0-100): ", 0, 100);
        IO.println("\nmodel.Grade confirmation");
        IO.println("model.Student: " + student.getName());
        IO.println("model.Subject: %s (%s)".formatted(
                subject.getSubjectName(), subject.getSubjectType().toLowerCase()));
        IO.println("model.Grade: %.2f%%".formatted(value));

        String confirmation = inputReader.readRequired("Save grade? (Y/N): ");
        if (confirmation.equalsIgnoreCase("Y")) {
            gradeManager.recordGrade(student, subject, value);
            IO.println("model.Grade recorded successfully!");
        } else {
            IO.println("model.Grade discarded.");
        }
    }

    /**
     * Prompts for and creates a subject of the selected type.
     *
     * @param subjectTypeChoice {@code 1} for core or another value for elective
     * @return the selected subject
     */
    public Subject chooseSubject(int subjectTypeChoice) {
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

        int subjectChoice = inputReader.readInt("Select subject: ", 1, names.length);
        String name = names[subjectChoice - 1];
        String code = subjectTypeChoice == 1
                ? "CORE%03d".formatted(subjectChoice)
                : "ELEC%03d".formatted(subjectChoice);

        return subjectTypeChoice == 1
                ? new CoreSubject(name, code)
                : new ElectiveSubject(name, code);
    }
}
