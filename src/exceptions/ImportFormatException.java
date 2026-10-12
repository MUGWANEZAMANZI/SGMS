package exceptions;

public class ImportFormatException extends IllegalArgumentException {
    public ImportFormatException(String message) {
        super(message);
    }
}
