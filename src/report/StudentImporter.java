package report;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Imports student records from a file.
 */
public interface StudentImporter {
    /**
     * Reads student records from a file.
     *
     * @param file source file
     * @return imported records
     * @throws IOException when the file cannot be read
     */
    List<StudentRecord> importStudents(Path file) throws IOException;
}
