package report;

import java.io.IOException;
import java.nio.file.Path;


public interface GradeReportImporter {
    /**
     * Imports a report to a file.
     *
     * @param report report to import
     * @param file source file
     * @throws IOException if the file cannot be written
     */

    void importReport(GradeReport report, Path file) throws IOException;
}
