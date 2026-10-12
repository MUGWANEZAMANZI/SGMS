package exceptions;

public class InvalidGradeException extends InvalidInputException {
    public InvalidGradeException(double grade) {
        super("Grade must be between 0 and 100: " + grade);
    }
}
