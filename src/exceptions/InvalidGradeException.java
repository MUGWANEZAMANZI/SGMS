package exceptions;

/**
 * Indicates that a grade is outside the accepted percentage range.
 */
public class InvalidGradeException extends InvalidInputException {

    /**
     * Constructs a new InvalidGradeException with the specified grade.
     *
     * @param grade the invalid grade
     */
    public InvalidGradeException(double grade) {
        super("Grade must be between 0 and 100: " + grade);
    }
}
