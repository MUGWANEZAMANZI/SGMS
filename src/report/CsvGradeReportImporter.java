package report;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Exports grade reports in CSV format.
 */
public final class CsvGradeReportImporter implements GradeReportImporter {

    /**
     * Exports a report to a CSV file.
     *
     * @param report report to export
     * @param file destination file
     * @throws IOException if the file cannot be written
     */
    @Override
    public ImportedGradeReport importReport(Path file) throws IOException {
        try (var lines = java.nio.file.Files.lines(file)) {
            String[] fields = lines.skip(1)
                    .findFirst()
                    .orElseThrow(() -> new IOException("CSV report has no data row"))
                    .split(",", -1);
            if (fields.length < 5) {
                throw new IOException("CSV report requires at least 5 columns");
            }
            return new ImportedGradeReport(
                    fields[0].trim(),
                    Double.parseDouble(fields[1].trim()),
                    Double.parseDouble(fields[2].trim()),
                    Double.parseDouble(fields[3].trim()),
                    Boolean.parseBoolean(fields[4].trim()),
                    java.util.List.of());
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid numeric value in CSV report", exception);
        }
    }
}
