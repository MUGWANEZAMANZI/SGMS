package report;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Exports grade reports to files.
 */
public interface GradeReportExporter {
    /**
     * Exports a report to a file.
     *
     * @param report report to export
     * @param file destination file
     * @throws IOException if the file cannot be written
     */
    void export(GradeReport report, Path file) throws IOException;
}