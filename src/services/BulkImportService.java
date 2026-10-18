package services;

import manager.GradeManager;
import manager.StudentManager;
import exceptions.ImportFormatException;
import exceptions.StudentNotFoundException;
import logging.ApplicationLogger;
import model.CoreSubject;
import model.ElectiveSubject;
import model.Student;
import model.Subject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import batch.ParallelBatchProcessor;
import audit.AuditTrail;

/**
 * Imports grades from CSV files into student and grade managers.
 */
public class BulkImportService {
    private static final String COMMA_DELIMITER = ",";
    private final StudentManager studentManager;
    private final GradeManager gradeManager;

    /**
     * Creates an import service with the supplied managers.
     *
     * @param studentManager manager used to find students
     * @param gradeManager manager used to record grades
     */
    public BulkImportService(StudentManager studentManager, GradeManager gradeManager) {
        this.studentManager = studentManager;
        this.gradeManager = gradeManager;
    }

    /**
     * Imports grades from a CSV file and writes a failure log.
     *
     * @param filePath path to the CSV file
     * @return a summary of imported and failed rows
     * @throws IOException if the input file or failure log cannot be accessed
     */
    public ImportSummary importCSV(String filePath) throws IOException {
        List<String> failures = new ArrayList<>();
        int total = 0;
        int imported = 0;
        try (var lines = Files.lines(Path.of(filePath))) {
            int rowNumber = 0;
            for (String line : (Iterable<String>) lines::iterator) {
                rowNumber++;
                if (rowNumber == 1 && line.toLowerCase().startsWith("studentid")) {
                    continue;
                }
                total++;
                String[] fields = line.split(COMMA_DELIMITER, -1);
                if (fields.length != 4) {
                    failures.add("Row " + rowNumber + ": expected 4 columns");
                    continue;
                }
                try {
                    Student student = studentManager.findStudentById(fields[0].trim());
                    if (student == null) {
                        throw new StudentNotFoundException(fields[0].trim());
                    }
                    String type = fields[2].trim();
                    Subject subject = type.equalsIgnoreCase("Core")
                            ? new CoreSubject(fields[1].trim(), "IMPORT-CORE-" + rowNumber)
                            : type.equalsIgnoreCase("Elective")
                            ? new ElectiveSubject(fields[1].trim(), "IMPORT-ELECTIVE-" + rowNumber)
                            : null;
                    if (subject == null) {
                        throw new ImportFormatException(
                                "Row " + rowNumber + ": invalid subject type");
                    }
                    double value = Double.parseDouble(fields[3].trim());
                    gradeManager.recordGrade(student, subject, value);
                    imported++;
                } catch (NumberFormatException
                         | exceptions.InvalidInputException exception) {
                    failures.add("Row " + rowNumber + ": " + exception.getMessage());
                }
            }
        }
        Path logPath = Path.of("imports", "import_log_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                + ".txt");
        Files.createDirectories(logPath.getParent());
        Files.write(logPath, failures);
        ImportSummary summary = new ImportSummary(total, imported, failures.size(), logPath.toString());
        IO.println("Import summary: " + summary.importedRows() + " imported, "
                + summary.failedRows() + " failed of " + summary.totalRows());
        IO.println("Import log: " + summary.logFile());
        ApplicationLogger.info("CSV import completed: " + summary);
        return summary;
    }

    /**
     * Imports CSV rows concurrently using a bounded worker pool.
     *
     * @param filePath CSV source
     * @param workerCount number of processing workers
     * @param auditTrail audit sink
     * @return import summary
     */
    public ImportSummary importCSVParallel(
            String filePath,
            int workerCount,
            AuditTrail auditTrail) throws IOException {
        List<String> rows = Files.readAllLines(Path.of(filePath));
        List<String> dataRows = rows.stream()
                .skip(rows.isEmpty() ? 0 : 1)
                .filter(row -> !row.isBlank())
                .toList();
        try (ParallelBatchProcessor<String, String> processor =
                     new ParallelBatchProcessor<>(workerCount,
                             row -> processParallelRow(row, auditTrail))) {
            List<String> failures = processor.process(dataRows).stream()
                    .filter(java.util.Objects::nonNull)
                    .toList();
            int imported = dataRows.size() - failures.size();
            auditTrail.record("BULK_IMPORT",
                    "Imported " + imported + " of " + dataRows.size());
            return new ImportSummary(
                    dataRows.size(), imported, failures.size(), "parallel-processing");
        }
    }

    private String processParallelRow(String line, AuditTrail auditTrail) {
        try {
            String[] fields = line.split(COMMA_DELIMITER, -1);
            if (fields.length != 4) {
                return "Expected 4 columns";
            }
            Student student = studentManager.findStudentById(fields[0].trim());
            if (student == null) {
                throw new StudentNotFoundException(fields[0].trim());
            }
            String type = fields[2].trim();
            Subject subject = type.equalsIgnoreCase("Core")
                    ? new CoreSubject(fields[1].trim(), "IMPORT-CORE")
                    : type.equalsIgnoreCase("Elective")
                    ? new ElectiveSubject(fields[1].trim(), "IMPORT-ELECTIVE")
                    : null;
            if (subject == null) {
                throw new ImportFormatException("Invalid subject type");
            }
            gradeManager.recordGrade(
                    student, subject, Double.parseDouble(fields[3].trim()));
            auditTrail.record("GRADE_IMPORT", fields[0].trim());
            return null;
        } catch (StudentNotFoundException | ImportFormatException
                 | NumberFormatException exception) {
            auditTrail.record("GRADE_IMPORT_FAILURE", exception.getMessage());
            return exception.getMessage();
        }
    }
}
