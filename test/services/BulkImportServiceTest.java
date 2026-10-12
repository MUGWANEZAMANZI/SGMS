package services;

import manager.GradeManager;
import manager.StudentManager;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BulkImportServiceTest {

    @Test
    void importsValidCsvFile() throws Exception {
        StudentManager studentManager = new StudentManager();
        GradeManager gradeManager = new GradeManager();

        Student[] students = addStudents(studentManager, 5);
        Path csvFile = Files.createTempFile("grades-", ".csv");
        StringBuilder csv = new StringBuilder("studentId,subject,type,grade\n");
        for (Student student : students) {
            for (int grade = 0; grade < 20; grade++) {
                csv.append(student.getStudentId())
                        .append(",Mathematics,Core,")
                        .append(grade + 1)
                        .append("\n");
            }
        }
        Files.writeString(csvFile, csv);

        BulkImportService service =
                new BulkImportService(studentManager, gradeManager);

        try {
            assertEquals(100, service.importCSV(csvFile.toString()).importedRows());
            assertEquals(100, gradeManager.getGradeCount());
        } finally {
            Files.deleteIfExists(csvFile);
        }
    }

    @Test
    void summarizesMalformedMissingAndUnknownRows() throws Exception {
        StudentManager studentManager = new StudentManager();
        GradeManager gradeManager = new GradeManager();
        Student student = addStudents(studentManager, 1)[0];
        Path csvFile = Files.createTempFile("invalid-grades-", ".csv");
        Files.writeString(csvFile,
                "studentId,subject,type,grade\n"
                        + student.getStudentId() + ",Math,Core,not-a-number\n"
                        + "STU999,Math,Core,80\n"
                        + "bad,row\n"
                        + student.getStudentId() + ",Art,Elective,70\n"
                        + student.getStudentId() + ",Math,Other,80\n");

        try {
            ImportSummary summary = new BulkImportService(studentManager, gradeManager)
                    .importCSV(csvFile.toString());
            assertEquals(5, summary.totalRows());
            assertEquals(1, summary.importedRows());
            assertEquals(4, summary.failedRows());
            assertTrue(Files.exists(Path.of(summary.logFile())));
            assertEquals(1, gradeManager.getGradeCount());
        } finally {
            Files.deleteIfExists(csvFile);
        }
    }

    private static Student[] addStudents(StudentManager studentManager, int count) {
        Student[] students = new Student[count];
        for (int index = 0; index < count; index++) {
            Student student = new RegularStudent(
                    "Student" + index,
                    20,
                    "student" + index + "@example.com",
                    "071234567" + index);
            studentManager.addStudent(student);
            students[index] = student;
        }
        return students;
    }
}