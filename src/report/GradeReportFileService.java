package report;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

/**
 * Selects a report importer or exporter from a file extension.
 */
public final class GradeReportFileService {

    /**
     * Exports a report using the file extension.
     *
     * @param report report to export
     * @param file destination file
     * @throws IOException if the file cannot be written
     */
    public void export(GradeReport report, Path file) throws IOException {
        Objects.requireNonNull(report, "Report cannot be null");
        exporterFor(file).export(report, file);
    }

    /**
     * Imports a report using the file extension.
     *
     * @param file source file
     * @return imported report data
     * @throws IOException if the file cannot be read
     */
    public ImportedGradeReport importReport(Path file) throws IOException {
        return importerFor(file).importReport(file);
    }

    private static GradeReportExporter exporterFor(Path file) {
        String extension = extension(file);
        return switch (extension) {
            case "txt" -> new TextGradeReportExporter();
            case "csv" -> new CsvGradeReportExporter();
            case "json" -> new JsonGradeReportExporter();
            case "bin" -> new BinaryGradeReportExporter();
            default -> throw new IllegalArgumentException(
                    "Unsupported report format: ." + extension);
        };
    }

    private static GradeReportImporter importerFor(Path file) {
        String extension = extension(file);
        return switch (extension) {
            case "csv" -> new CsvGradeReportImporter();
            case "json" -> new JsonGradeReportImporter();
            case "bin" -> new BinaryGradeReportImporter();
            default -> throw new IllegalArgumentException(
                    "Unsupported report import format: ." + extension);
        };
    }

    private static String extension(Path file) {
        Objects.requireNonNull(file, "File cannot be null");
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        if (dot < 1 || dot == name.length() - 1) {
            throw new IllegalArgumentException("File must have a supported extension");
        }
        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
