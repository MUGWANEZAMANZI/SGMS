package report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Exports grade reports in CSV format.
 */
public class CsvGradeReportExporter implements GradeReportExporter {

    /**
     * Exports a report to a CSV file.
     *
     * @param report report to export
     * @param file destination file
     * @throws IOException if the file cannot be written
     */
    @Override
    public void export(GradeReport report, Path file) throws IOException {
        String rawCsv = report.generateCsvReport();
        Files.write(file, rawCsv.getBytes());
    }
}
