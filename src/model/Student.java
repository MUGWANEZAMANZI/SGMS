package model;

import exceptions.CapacityExceededException;
import exceptions.InvalidInputException;


import java.util.Objects;
import validation.ValidationPatterns;

/**
 * Base class for students and their recorded grades.
 */
public abstract class Student {

    private static int nextStudentNumber = 1;
    private final String studentId;
    private final Grade[] grades = new Grade[200];
    private int gradeCount;

    private String name;
    private int age;
    private String email;
    private String phone;
    private Status status;


    protected Student(String name, int age, String email, String phone) {
        String validatedName  = requireNonBlank(name, "Name");
        int    validatedAge   = validateAge(age);
        String validatedEmail = ValidationPatterns.requireEmail(email);
        String validatedPhone = ValidationPatterns.requirePhone(phone);

        // All checks passed — safe to claim an ID
        this.studentId = "STU%03d".formatted(nextStudentNumber++);
        this.name   = validatedName;
        this.age    = validatedAge;
        this.email  = validatedEmail;
        this.phone  = validatedPhone;
        this.status = Status.ACTIVE;
        }


    /**
     * Returns the generated student identifier.
     *
     * @return this student's identifier
     */
    public final String getStudentId() {
        return studentId;
    }

    /**
     * Returns the student's name.
     *
     * @return the student's name
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the student's name.
     *
     * @param name the new non-blank name
     * @throws InvalidInputException if {@code name} is blank
     */
    public void setName(String name) {
        this.name = requireNonBlank(name, "Name");
    }

    /**
     * Returns the student's age.
     *
     * @return the student's age
     */
    public int getAge() {
        return age;
    }

    /**
     * Updates the student's age.
     *
     * @param age the new age, from 1 through 120
     * @throws InvalidInputException if {@code age} is outside the valid range
     */
    public void setAge(int age) {
        this.age = validateAge(age);
    }

    /**
     * Returns the student's email address.
     *
     * @return the student's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Updates the student's email address.
     *
     * @param email the new non-blank email address
     * @throws InvalidInputException if {@code email} is blank
     */
    public void setEmail(String email) {
        this.email = ValidationPatterns.requireEmail(email);
    }

    /**
     * Returns the student's phone number.
     *
     * @return the student's phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Updates the student's phone number.
     *
     * @param phone the new non-blank phone number
     * @throws InvalidInputException if {@code phone} is blank
     */
    public void setPhone(String phone) {
        this.phone = ValidationPatterns.requirePhone(phone);
    }

    /**
     * Returns the student's status.
     *
     * @return the current status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Updates the student's status.
     *
     * @param status the new status
     * @throws NullPointerException if {@code status} is {@code null}
     */
    public void setStatus(Status status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    /**
     * Adds a grade to the student's grade list.
     *
     * @param grade grade to add
     * @throws CapacityExceededException if the grade capacity has been reached
     * @throws NullPointerException if {@code grade} is {@code null}
     */
    public synchronized void addGrade(Grade grade) {
        if (gradeCount >= grades.length) {
            throw new CapacityExceededException("model.Grade capacity has been reached");
        }
        grades[gradeCount++] = Objects.requireNonNull(grade, "model.Grade cannot be null");
    }

    /**
     * Returns the student's recorded grades.
     *
     * @return a copy of the recorded grades
     */
    public synchronized Grade[] getGrades() {
        return java.util.Arrays.copyOf(grades, gradeCount);
    }

    /**
     * Calculates the student's average percentage grade.
     *
     * @return the average grade, or {@code 0.0} if no grades are recorded
     */
    public synchronized double calculateAverageGrade() {
        if (gradeCount == 0) {
            return 0.0;
        }

        double total = 0.0;
        for (int index = 0; index < gradeCount; index++) {
            total += grades[index].value();
        }
        return total / gradeCount;
    }

    /**
     * Determines whether the student meets the passing threshold.
     *
     * @return {@code true} when the student has grades and meets the threshold
     */
    public synchronized boolean isPassing() {
        return gradeCount > 0 && calculateAverageGrade() >= getPassingGrade();
    }

    /**
     * Returns the student category.
     *
     * @return the student category
     */
    public abstract String getStudentType();

    /**
     * Returns the percentage required for the student to pass.
     *
     * @return the passing percentage
     */
    public abstract double getPassingGrade();

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidInputException(fieldName + " cannot be blank");
        }
        return value.trim();
    }

    private static int validateAge(int age) {
        if (age < 1 || age > 120) {
            throw new InvalidInputException("Age must be between 1 and 120");
        }
        return age;
    }

    /**
     * Represents the lifecycle status of a student.
     */
    public enum Status {
        ACTIVE,
        INACTIVE,
        GRADUATED
    }
}
