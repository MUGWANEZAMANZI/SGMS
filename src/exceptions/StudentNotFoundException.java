package exceptions;

/**
 * Indicates that a requested student does not exist.
 */
public class StudentNotFoundException extends InvalidInputException {

    /**
     * Constructs a new StudentNotFoundException with the specified student ID.
     *
     * @param studentId the ID of the student that was not found
     */
    public StudentNotFoundException(String studentId) {
        super("Student not found: " + studentId);
    }
}
