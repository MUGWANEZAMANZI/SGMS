package report;

import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;
import java.nio.file.Path;

public class TextGradeeReportExporter implements GradeReportExporter {

@Override
public void export(GradeReport report, Path file) throws IOException {
        try (FileWriter writer = new FileWriter(file.toFile())) {
            writer.write(report.generateReport());
        }
    }
}
