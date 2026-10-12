package exceptions;

public class StudentNotFoundException extends IllegalArgumentException {
    public StudentNotFoundException(String studentId) {
        super("Student not found: " + studentId);
    }
}
