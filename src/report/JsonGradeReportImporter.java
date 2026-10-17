package report;

import java.io.IOException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Imports grade reports in CSV format.
 */
public class JsonGradeReportImporter implements GradeReportImporter {

    /**
     * Imports a report to a CSV file.
     *
     * @param report report to export
     * @param file destination file
     * @throws IOException if the file cannot be written
     */
    @Override
    public void importReport(GradeReport report, Path file) throws IOException {
        String rawJson = report.generateJsonReport();
        Files.write(file, rawJson.getBytes());
    }
}
