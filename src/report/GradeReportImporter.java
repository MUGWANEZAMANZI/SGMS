package report;

import java.io.IOException;
import java.nio.file.Path;


public interface GradeReportImporter {
    ImportedGradeReport importReport(Path file) throws IOException;
}
