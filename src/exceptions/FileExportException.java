package exceptions;

public class FileExportException extends IllegalStateException {
    public FileExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
