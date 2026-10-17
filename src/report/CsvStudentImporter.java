package report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Imports students from CSV with columns:
 * name,age,email,phone,type,status.
 */
public final class CsvStudentImporter implements StudentImporter {
    @Override
    public List<StudentRecord> importStudents(Path file) throws IOException {
        try (var lines = Files.lines(file)) {
            return lines.skip(1)
                    .filter(line -> !line.isBlank())
                    .map(CsvStudentImporter::parseLine)
                    .toList();
        }
    }

    private static StudentRecord parseLine(String line) {
        String[] fields = line.split(",", -1);
        if (fields.length != 6) {
            throw new IllegalArgumentException(
                    "Student CSV rows must contain 6 columns");
        }
        return new StudentRecord(
                fields[0].trim(),
                Integer.parseInt(fields[1].trim()),
                fields[2].trim(),
                fields[3].trim(),
                fields[4].trim(),
                fields[5].trim());
    }
}
