package services;

import manager.GradeManager;
import manager.StudentManager;
import model.Grade;
import model.RegularStudent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ImportPipelineTest {

    @TempDir
    Path tempDir;

    private BulkImportService service(StudentManager students) {
        return new BulkImportService(students, new GradeManager());
    }

    @Test
    void importCSVImportsGradesForExistingStudents() throws IOException {
        StudentManager students = new StudentManager();
        RegularStudent alice = new RegularStudent("Alice", 20, "alice@example.com", "0712345678");
        students.addStudent(alice);

        Path csv = tempDir.resolve("grades.csv");
        Files.writeString(csv,
                "StudentId,Subject,Type,Grade\n" +
                alice.getStudentId() + ",Mathematics,Core,85\n");

        ImportSummary summary = service(students).importCSV(csv.toString());

        assertEquals(1, summary.totalRows());
        assertEquals(1, summary.importedRows());
        assertEquals(0, summary.failedRows());
        assertEquals(1, alice.getGrades().length);
        assertEquals(85.0, alice.getGrades()[0].value());
    }

    @Test
    void importCSVSkipsRowsWithUnknownStudentId() throws IOException {
        StudentManager students = new StudentManager();

        Path csv = tempDir.resolve("grades.csv");
        Files.writeString(csv,
                "StudentId,Subject,Type,Grade\n" +
                "STU999,Mathematics,Core,85\n");

        ImportSummary summary = service(students).importCSV(csv.toString());

        assertEquals(1, summary.totalRows());
        assertEquals(0, summary.importedRows());
        assertEquals(1, summary.failedRows());
    }

    @Test
    void importCSVSkipsRowsWithBadGradeValue() throws IOException {
        StudentManager students = new StudentManager();
        RegularStudent alice = new RegularStudent("Alice", 20, "alice@example.com", "0712345678");
        students.addStudent(alice);

        Path csv = tempDir.resolve("grades.csv");
        Files.writeString(csv,
                "StudentId,Subject,Type,Grade\n" +
                alice.getStudentId() + ",Mathematics,Core,not-a-number\n");

        ImportSummary summary = service(students).importCSV(csv.toString());

        assertEquals(1, summary.totalRows());
        assertEquals(0, summary.importedRows());
        assertEquals(1, summary.failedRows());
        assertEquals(0, alice.getGrades().length);
    }

    @Test
    void importCSVSkipsRowsWithGradeOutOfRange() throws IOException {
        StudentManager students = new StudentManager();
        RegularStudent alice = new RegularStudent("Alice", 20, "alice@example.com", "0712345678");
        students.addStudent(alice);

        Path csv = tempDir.resolve("grades.csv");
        Files.writeString(csv,
                "StudentId,Subject,Type,Grade\n" +
                alice.getStudentId() + ",Mathematics,Core,150\n");

        ImportSummary summary = service(students).importCSV(csv.toString());

        assertEquals(1, summary.totalRows());
        assertEquals(0, summary.importedRows());
        assertEquals(1, summary.failedRows());
    }

    @Test
    void importCSVSkipsRowsWithWrongColumnCount() throws IOException {
        StudentManager students = new StudentManager();
        RegularStudent alice = new RegularStudent("Alice", 20, "alice@example.com", "0712345678");
        students.addStudent(alice);

        Path csv = tempDir.resolve("grades.csv");
        Files.writeString(csv,
                "StudentId,Subject,Type,Grade\n" +
                alice.getStudentId() + ",Mathematics,Core\n");   // only 3 columns

        ImportSummary summary = service(students).importCSV(csv.toString());

        assertEquals(1, summary.totalRows());
        assertEquals(0, summary.importedRows());
        assertEquals(1, summary.failedRows());
    }

    @Test
    void importCSVSkipsRowsWithInvalidSubjectType() throws IOException {
        StudentManager students = new StudentManager();
        RegularStudent alice = new RegularStudent("Alice", 20, "alice@example.com", "0712345678");
        students.addStudent(alice);

        Path csv = tempDir.resolve("grades.csv");
        Files.writeString(csv,
                "StudentId,Subject,Type,Grade\n" +
                alice.getStudentId() + ",Mathematics,UNKNOWN,85\n");

        ImportSummary summary = service(students).importCSV(csv.toString());

        assertEquals(1, summary.totalRows());
        assertEquals(0, summary.importedRows());
        assertEquals(1, summary.failedRows());
    }

    @Test
    void importCSVImportsMultipleRowsMixedResults() throws IOException {
        StudentManager students = new StudentManager();
        RegularStudent alice = new RegularStudent("Alice", 20, "alice@example.com", "0712345678");
        students.addStudent(alice);

        Path csv = tempDir.resolve("grades.csv");
        Files.writeString(csv,
                "StudentId,Subject,Type,Grade\n" +
                alice.getStudentId() + ",Mathematics,Core,80\n" +
                "STU999,Physics,Core,90\n" +
                alice.getStudentId() + ",English,Elective,75\n");

        ImportSummary summary = service(students).importCSV(csv.toString());

        assertEquals(3, summary.totalRows());
        assertEquals(2, summary.importedRows());
        assertEquals(1, summary.failedRows());
        assertEquals(2, alice.getGrades().length);
    }

    @Test
    void importCSVSkipsHeaderAndCountsOnlyDataRows() throws IOException {
        StudentManager students = new StudentManager();
        RegularStudent alice = new RegularStudent("Alice", 20, "alice@example.com", "0712345678");
        students.addStudent(alice);

        Path csv = tempDir.resolve("grades.csv");
        Files.writeString(csv,
                "studentid,subject,type,grade\n" +
                alice.getStudentId() + ",Math,Core,70\n");

        ImportSummary summary = service(students).importCSV(csv.toString());

        assertEquals(1, summary.totalRows());
        assertEquals(1, summary.importedRows());
    }

    @Test
    void importCSVHandlesEmptyFile() throws IOException {
        Path csv = tempDir.resolve("empty.csv");
        Files.writeString(csv, "");

        ImportSummary summary = service(new StudentManager()).importCSV(csv.toString());

        assertEquals(0, summary.totalRows());
        assertEquals(0, summary.importedRows());
        assertEquals(0, summary.failedRows());
    }

    @Test
    void importCSVCoreSubjectGradeRecordedWithCorrectType() throws IOException {
        StudentManager students = new StudentManager();
        RegularStudent alice = new RegularStudent("Alice", 20, "alice@example.com", "0712345678");
        students.addStudent(alice);

        Path csv = tempDir.resolve("grades.csv");
        Files.writeString(csv,
                "StudentId,Subject,Type,Grade\n" +
                alice.getStudentId() + ",Chemistry,Core,88\n");

        service(students).importCSV(csv.toString());

        Grade grade = alice.getGrades()[0];
        assertEquals("Core", grade.subject().getSubjectType());
        assertEquals("Chemistry", grade.subject().getSubjectName());
    }
}
