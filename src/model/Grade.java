package model;

import java.time.LocalDate;
import java.util.Objects;

public class Grade {

    private static int nextGradeNumber = 1;

    private final String gradeId;
    private final String studentId;
    private final Subject subject;
    private final double value;
    private final LocalDate recordedAt;

    public Grade(String studentId, Subject subject, double value) {
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("model.Student ID cannot be blank");
        }
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("model.Grade must be between 0 and 100");
        }

        this.gradeId = "GRD%03d".formatted(nextGradeNumber++);
        this.studentId = studentId;
        this.subject = Objects.requireNonNull(subject, "model.Subject cannot be null");
        this.value = value;
        this.recordedAt = LocalDate.now();
    }

    public String getGradeId() {
        return gradeId;
    }

    public String getStudentId() {
        return studentId;
    }

    public Subject getSubject() {
        return subject;
    }

    public String get

    public double getValue() {
        return value;
    }

    public LocalDate getRecordedAt() {
        return recordedAt;
    }

    public String formattedDate() {
        return recordedAt.toString();
    }

    public void displayGradeDetails() {
        IO.println("%-8s %-22s %-10s %7.2f%%  %-12s".formatted(
                gradeId,
                subject.getSubjectName(),
                subject.getSubjectType(),
                value,
                formattedDate()));
    }
}
