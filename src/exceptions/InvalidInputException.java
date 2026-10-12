package exceptions;

/**
 * Indicates that an input value is invalid.
 */
public class InvalidInputException extends IllegalArgumentException {

    /**
     * Constructs a new InvalidInputException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidInputException(String message) {
        super(message);
    }
}
