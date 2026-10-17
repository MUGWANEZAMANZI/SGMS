package report;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Exports grade reports in CSV format.
 */
public final class BinaryGradeReportImporter implements GradeReportImporter {

    /**
     * Exports a report to a CSV file.
     *
     * @param report report to export
     * @param file destination file
     * @throws IOException if the file cannot be written
     */
    @Override
    public ImportedGradeReport importReport(Path file) throws IOException {
        try (var input = new java.io.DataInputStream(
                java.nio.file.Files.newInputStream(file))) {
            String studentName = input.readUTF();
            double overall = input.readDouble();
            double core = input.readDouble();
            double elective = input.readDouble();
            boolean passing = input.readBoolean();
            int count = input.readInt();
            if (count < 0) {
                throw new IOException("Invalid grade count: " + count);
            }
            var grades = new java.util.ArrayList<ImportedGradeReport.ImportedGrade>();
            for (int index = 0; index < count; index++) {
                grades.add(new ImportedGradeReport.ImportedGrade(
                        input.readUTF(),
                        input.readUTF(),
                        input.readUTF(),
                        input.readDouble()));
            }
            return new ImportedGradeReport(
                    studentName, overall, core, elective, passing, grades);
        }
    }
}
