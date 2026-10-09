package services;

import contract.CSVParser;
import manager.GradeManager;
import manager.StudentManager;
import model.CoreSubject;
import model.ElectiveSubject;
import model.Student;
import model.Subject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BulkImportService implements CSVParser {
    private final StudentManager studentManager;
    private final GradeManager gradeManager;

    public BulkImportService(StudentManager studentManager, GradeManager gradeManager) {
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
    }

    @Override
    public void importCSV(String filePath) throws IOException {
        try (var lines = Files.lines(Path.of(filePath))) {
            int rowNumber = 0;
            int imported = 0;
            int failed = 0;
            for (String line : (Iterable<String>) lines::iterator) {
                rowNumber++;
                if (rowNumber == 1 && line.toLowerCase().startsWith("studentid")) {
                    continue;
                }
                String[] fields = line.split(COMMA_DELIMITER, -1);
                if (fields.length != 4) {
                    failed++;
                    continue;
                }
                try {
                    Student student = studentManager.findStudentById(fields[0].trim());
                    if (student == null) {
                        throw new IllegalArgumentException("Student not found");
                    }
                    String type = fields[2].trim();
                    Subject subject = type.equalsIgnoreCase("Core")
                            ? new CoreSubject(fields[1].trim(), "IMPORT-CORE-" + rowNumber)
                            : type.equalsIgnoreCase("Elective")
                            ? new ElectiveSubject(fields[1].trim(), "IMPORT-ELECTIVE-" + rowNumber)
                            : null;
                    if (subject == null) {
                        throw new IllegalArgumentException("Invalid subject type");
                    }
                    double value = Double.parseDouble(fields[3].trim());
                    gradeManager.recordGrade(student, subject, value);
                    imported++;
                } catch (IllegalArgumentException exception) {
                    failed++;
                }
            }
            IO.println("Imported grades: " + imported);
            IO.println("Failed rows: " + failed);
        }
    }
}
