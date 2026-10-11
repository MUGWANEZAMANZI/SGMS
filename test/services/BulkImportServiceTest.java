package services;

import manager.GradeManager;
import manager.StudentManager;
import model.RegularStudent;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BulkImportServiceTest {

    @Test
    void importsValidCsvFile() throws Exception {
        StudentManager studentManager = new StudentManager();
        GradeManager gradeManager = new GradeManager();

        addStudents(studentManager, 5);

        BulkImportService service =
                new BulkImportService(studentManager, gradeManager);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));

            service.importCSV("imports/grades_100.csv");
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals(100, gradeManager.getGradeCount());
    }

    private static void addStudents(StudentManager studentManager, int count) {
        for (int index = 1; index <= count; index++) {
            studentManager.addStudent(new RegularStudent(
                    "Student" + index,
                    20,
                    "student" + index + "@example.com",
                    "071234567" + index
            ));
        }
    }
}