package model;

import java.util.Objects;

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
        this.studentId = "STU%03d".formatted(nextStudentNumber++);
        this.name = requireNonBlank(name, "Name");
        this.age = validateAge(age);
        this.email = requireNonBlank(email, "Email");
        this.phone = requireNonBlank(phone, "Phone");
        this.status = Status.ACTIVE;
    }

    public final String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = requireNonBlank(name, "Name");
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = validateAge(age);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = requireNonBlank(email, "Email");
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = requireNonBlank(phone, "Phone");
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null");
    }

    public void addGrade(Grade grade) {
        if (gradeCount >= grades.length) {
            throw new IllegalStateException("model.Grade capacity has been reached");
        }
        grades[gradeCount++] = Objects.requireNonNull(grade, "model.Grade cannot be null");
    }

    public Grade[] getGrades() {
        return java.util.Arrays.copyOf(grades, gradeCount);
    }

    public double calculateAverageGrade() {
        if (gradeCount == 0) {
            return 0.0;
        }
        double total = 0.0;
        for (int index = 0; index < gradeCount; index++) {
            total += grades[index].getValue();
        }
        return total / gradeCount;
    }

    public boolean isPassing() {
        return gradeCount > 0 && calculateAverageGrade() >= getPassingGrade();
    }

    public abstract String getStudentType();

    public abstract double getPassingGrade();

    public abstract void displayStudentDetails();

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }

    private static int validateAge(int age) {
        if (age < 1 || age > 120) {
            throw new IllegalArgumentException("Age must be between 1 and 120");
        }
        return age;
    }

    public enum Status {
        ACTIVE,
        INACTIVE,
        GRADUATED
    }
}
