package exceptions;

/**
 * Indicates that imported data does not match the expected format.
 */
public class ImportFormatException extends InvalidInputException {

    /**
     * Constructs a new ImportFormatException with the specified detail message.
     *
     * @param message the detail message
     */
    public ImportFormatException(String message) {
        super(message);
    }
}
