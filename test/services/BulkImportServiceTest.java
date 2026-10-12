package services;

import manager.GradeManager;
import manager.StudentManager;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
            service.importCSV(csvFile.toString());
            assertEquals(100, gradeManager.getGradeCount());
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