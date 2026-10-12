package exceptions;

public class StudentNotFoundException extends InvalidInputException {
    public StudentNotFoundException(String studentId) {
        super("Student not found: " + studentId);
    }
}
