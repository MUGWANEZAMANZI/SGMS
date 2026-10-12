package exceptions;

/**
 * Indicates that a fixed-capacity collection cannot accept another item.
 */
public class CapacityExceededException extends IllegalStateException {

    /**
     * Constructs a new CapacityExceededException with the specified detail message.
     *
     * @param message the detail message
     */
    public CapacityExceededException(String message) {
        super(message);
    }
}
