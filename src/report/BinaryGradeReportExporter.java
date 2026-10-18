package report;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Exports grade reports in CSV format.
 */
public final class BinaryGradeReportExporter implements GradeReportExporter {

    /**
     * Exports a report to a CSV file.
     *
     * @param report report to export
     * @param file destination file
     * @throws IOException if the file cannot be written
     */
    @Override
    public void export(GradeReport report, Path file) throws IOException {
        try (var output = new java.io.DataOutputStream(
                java.nio.file.Files.newOutputStream(file))) {
            output.writeUTF(report.student().getName());
            output.writeDouble(report.overallAverage());
            output.writeDouble(report.coreAverage());
            output.writeDouble(report.electiveAverage());
            output.writeBoolean(report.passing());
            model.Grade[] grades = report.grades();
            output.writeInt(grades.length);
            for (model.Grade grade : grades) {
                output.writeUTF(grade.subject().getSubjectName());
                output.writeUTF(grade.subject().getSubjectCode());
                output.writeUTF(grade.subject().getSubjectType());
                output.writeDouble(grade.value());
            }
        }
    }
}
