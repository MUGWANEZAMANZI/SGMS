package exceptions;

public class CapacityExceededException extends IllegalStateException {
    public CapacityExceededException(String message) {
        super(message);
    }
}
