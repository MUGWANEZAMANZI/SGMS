package exceptions;

public class InvalidGradeException extends IllegalArgumentException {
    public InvalidGradeException(double grade) {
        super("Grade must be between 0 and 100: " + grade);
    }
}
