package report;

import manager.StudentManager;
import model.HonorsStudent;
import model.RegularStudent;
import model.Student;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Imports student records and adds validated students to the manager.
 */
public final class StudentImportService {
    private final StudentManager studentManager;

    public StudentImportService(StudentManager studentManager) {
        this.studentManager = Objects.requireNonNull(studentManager);
    }

    /**
     * Imports students according to the file extension.
     *
     * @param file source file
     * @return number of students added
     * @throws IOException when the file cannot be read
     */
    public int importStudents(Path file) throws IOException {
        Objects.requireNonNull(file, "File cannot be null");
        StudentImporter importer = importerFor(file);
        List<StudentRecord> records = importer.importStudents(file);
        for (StudentRecord record : records) {
            studentManager.addStudent(createStudent(record));
        }
        return records.size();
    }

    private static StudentImporter importerFor(Path file) {
        String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
        if (name.endsWith(".csv")) {
            return new CsvStudentImporter();
        }
        if (name.endsWith(".json")) {
            return new JsonStudentImporter();
        }
        if (name.endsWith(".bin")) {
            return new BinaryStudentImporter();
        }
        throw new IllegalArgumentException("Unsupported student format: " + name);
    }

    private static Student createStudent(StudentRecord record) {
        Student student = switch (record.type().toLowerCase(Locale.ROOT)) {
            case "regular" -> new RegularStudent(
                    record.name(), record.age(), record.email(), record.phone());
            case "honors" -> new HonorsStudent(
                    record.name(), record.age(), record.email(), record.phone());
            default -> throw new IllegalArgumentException(
                    "Unsupported student type: " + record.type());
        };
        if (record.status() != null && !record.status().isBlank()) {
            student.setStatus(Student.Status.valueOf(
                    record.status().toUpperCase(Locale.ROOT)));
        }
        return student;
    }
}
