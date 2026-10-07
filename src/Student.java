import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Student {

    private static int nextStudentNumber = 1;

    private final String studentId;
    private final List<Grade> grades = new ArrayList<>();

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
        grades.add(Objects.requireNonNull(grade, "Grade cannot be null"));
    }

    public List<Grade> getGrades() {
        return List.copyOf(grades);
    }

    public double calculateAverageGrade() {
        return grades.stream()
                .mapToDouble(Grade::value)
                .average()
                .orElse(0.0);
    }

    public boolean isPassing() {
        return !grades.isEmpty() && calculateAverageGrade() >= getPassingGrade();
    }

    public abstract String getStudentType();

    public abstract double getPassingGrade();

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
