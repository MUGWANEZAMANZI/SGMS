package model;

import exceptions.InvalidGradeException;
import exceptions.InvalidInputException;
import java.time.LocalDate;
import java.util.Objects;
import validation.ValidationPatterns;

/**
 * Represents a percentage grade recorded for a student and subject.
 */
public record Grade(String gradeId, String studentId, Subject subject, double value, LocalDate recordedAt) {

    private static int nextGradeNumber = 1;

    /**
     * Creates a grade for a student and subject.
     *
     * @param studentId identifier of the student
     * @param subject   subject receiving the grade
     * @param value     percentage value from 0 through 100
     * @throws InvalidInputException if {@code studentId} is blank
     * @throws InvalidGradeException if {@code value} is outside the valid range
     * @throws NullPointerException  if {@code subject} is {@code null}
     */
    public Grade(String studentId, Subject subject, double value) {
        this(studentId, subject, value, LocalDate.now());
    }

    /**
     * Creates a grade with a supplied ISO-valid recording date.
     *
     * @param studentId  identifier of the student
     * @param subject    subject receiving the grade
     * @param value      percentage value from 0 through 100
     * @param recordedAt recording date
     */
    public Grade(String studentId, Subject subject, double value, LocalDate recordedAt) {
        String normalizedStudentId = ValidationPatterns.requireStudentId(studentId);
        if (value < 0 || value > 100) {
            throw new InvalidGradeException(value);
        }

        Objects.requireNonNull(subject, "Subject cannot be null");
        Objects.requireNonNull(recordedAt, "Recorded date cannot be null");


        //All checks passed - safe to claim an ID
        this("GRD%03d".formatted(nextGradeNumber++), normalizedStudentId, subject, value, recordedAt);


    }

    /**
     * Returns the grade identifier.
     *
     * @return the grade identifier
     */
    @Override
    public String gradeId() {
        return gradeId;
    }

    /**
     * Returns the associated student identifier.
     *
     * @return the student identifier
     */
    @Override
    public String studentId() {
        return studentId;
    }

    /**
     * Returns the graded subject.
     *
     * @return the subject
     */
    @Override
    public Subject subject() {
        return subject;
    }

    /**
     * Returns the percentage value.
     *
     * @return the percentage grade
     */
    @Override
    public double value() {
        return value;
    }

    /**
     * Returns the date on which the grade was recorded.
     *
     * @return the recording date
     */
    @Override
    public LocalDate recordedAt() {
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

    /**
     * Displays the grade details in a formatted row.
     */
    public void displayGradeDetails() {
        IO.println("%-8s %-22s %-10s %7.2f%%  %-12s".formatted(
                gradeId,
                subject.getSubjectName(),
                subject.getSubjectType(),
                value,
                formattedDate()));
    }
}
