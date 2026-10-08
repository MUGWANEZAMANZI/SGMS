package report;

import java.io.IOException;
import java.nio.file.Path;

public interface GradeReportExporter {
    void export(GradeReport report, Path file) throws IOException;
}