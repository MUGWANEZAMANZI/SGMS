package report;

import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;
import java.nio.file.Path;

/**
 * Exports grade reports as text.
 */
public class TextGradeReportExporter implements GradeReportExporter {

    /**
     * Exports a report to a text file.
     *
     * @param report report to export
     * @param file destination file
     * @throws IOException if the file cannot be written
     */
@Override
public void export(GradeReport report, Path file) throws IOException {
        try (FileWriter writer = new FileWriter(file.toFile())) {
            writer.write(report.generateTextReport());
        }
    }
}
