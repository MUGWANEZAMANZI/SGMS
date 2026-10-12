package exceptions;

/**
 * Indicates that a report or search result could not be exported.
 */
public class FileExportException extends IllegalStateException {

    /**
     * Constructs a new FileExportException with the specified detail message.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public FileExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
