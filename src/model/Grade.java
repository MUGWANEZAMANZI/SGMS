package model;

import exceptions.InvalidGradeException;
import exceptions.InvalidInputException;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a percentage grade recorded for a student and subject.
 */
public class Grade {

    private static int nextGradeNumber = 1;

    private final String gradeId;
    private final String studentId;
    private final Subject subject;
    private final double value;
    private final LocalDate recordedAt;

    /**
     * Creates a grade for a student and subject.
     *
     * @param studentId identifier of the student
     * @param subject subject receiving the grade
     * @param value percentage value from 0 through 100
     * @throws InvalidInputException if {@code studentId} is blank
     * @throws InvalidGradeException if {@code value} is outside the valid range
     * @throws NullPointerException if {@code subject} is {@code null}
     */
    public Grade(String studentId, Subject subject, double value) {
        if (studentId == null || studentId.isBlank()) {
            throw new InvalidInputException("model.Student ID cannot be blank");
        }
        if (value < 0 || value > 100) {
            throw new InvalidGradeException(value);
        }

        this.gradeId = "GRD%03d".formatted(nextGradeNumber++);
        this.studentId = studentId;
        this.subject = Objects.requireNonNull(subject, "model.Subject cannot be null");
        this.value = value;
        this.recordedAt = LocalDate.now();
    }

    /**
     * Returns the grade identifier.
     *
     * @return the grade identifier
     */
    public String getGradeId() {
        return gradeId;
    }

    /**
     * Returns the associated student identifier.
     *
     * @return the student identifier
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * Returns the graded subject.
     *
     * @return the subject
     */
    public Subject getSubject() {
        return subject;
    }

    /**
     * Returns the percentage value.
     *
     * @return the percentage grade
     */
    public double getValue() {
        return value;
    }

    /**
     * Returns the date on which the grade was recorded.
     *
     * @return the recording date
     */
    public LocalDate getRecordedAt() {
        return recordedAt;
    }

    /**
     * Returns the recording date in ISO-8601 form.
     *
     * @return the formatted date
     */
    public String formattedDate() {
        return recordedAt.toString();
    }

    /** Displays the grade details in a formatted row. */
    public void displayGradeDetails() {
        IO.println("%-8s %-22s %-10s %7.2f%%  %-12s".formatted(
                gradeId,
                subject.getSubjectName(),
                subject.getSubjectType(),
                value,
                formattedDate()));
    }
}
