package report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Imports grade reports in CSV format.
 */
public final class JsonGradeReportImporter implements GradeReportImporter {

    /**
     * Imports a report to a CSV file.
     *
     * @param file destination file
     * @throws IOException if the file cannot be written
     */
    @Override
    public ImportedGradeReport importReport(Path file) throws IOException {
        String json = Files.readString(file);
        try {
            return new ImportedGradeReport(
                    required(json, "student"),
                    Double.parseDouble(required(json, "overallAverage")),
                    Double.parseDouble(required(json, "coreAverage")),
                    Double.parseDouble(required(json, "electiveAverage")),
                    Boolean.parseBoolean(required(json, "passing")),
                    java.util.List.of());
        } catch (NumberFormatException exception) {
            throw new IOException("Invalid numeric value in JSON report", exception);
        }
    }

    private static String required(String json, String field) throws IOException {
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(
                "\"%s\"\\s*:\\s*(\"([^\"]*)\"|[^,}\\n]+)".formatted(field))
                .matcher(json);
        if (!matcher.find()) {
            throw new IOException("Missing JSON field: " + field);
        }
        String value = matcher.group(2);
        return value == null ? matcher.group(1).trim() : value;
    }
}
