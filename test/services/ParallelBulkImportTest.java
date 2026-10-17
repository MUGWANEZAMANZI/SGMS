package services;

import audit.AuditTrail;
import manager.GradeManager;
import manager.StudentManager;
import model.CoreSubject;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ParallelBulkImportTest {

    @Test
    void importsValidRowsInParallelAndAuditsEachSuccessfulRow() throws Exception {
        StudentManager studentManager = mock(StudentManager.class);
        GradeManager gradeManager = mock(GradeManager.class);
        AuditTrail auditTrail = mock(AuditTrail.class);
        Student student = new RegularStudent(
                "Alice", 20, "parallel@example.com", "0712345678");
        when(studentManager.findStudentById("STU001")).thenReturn(student);
        when(studentManager.findStudentById("STU002")).thenReturn(student);
        Path csv = Files.createTempFile("parallel-grades-", ".csv");
        Files.writeString(csv,
                "studentId,subject,type,grade\n"
                        + "STU001,Math,Core,85\n"
                        + "STU002,Science,Core,90\n");

        try {
            ImportSummary summary = new BulkImportService(studentManager, gradeManager)
                    .importCSVParallel(csv.toString(), 2, auditTrail);

            assertEquals(2, summary.totalRows());
            assertEquals(2, summary.importedRows());
            assertEquals(0, summary.failedRows());
            verify(gradeManager, times(2))
                    .recordGrade(eq(student), any(CoreSubject.class), anyDouble());
            verify(auditTrail, times(2)).record(eq("GRADE_IMPORT"), anyString());
            verify(auditTrail).record("BULK_IMPORT", "Imported 2 of 2");
        } finally {
            Files.deleteIfExists(csv);
        }
    }

    @Test
    void summarizesFailedRowsAndAuditsFailures() throws Exception {
        StudentManager studentManager = mock(StudentManager.class);
        GradeManager gradeManager = mock(GradeManager.class);
        AuditTrail auditTrail = mock(AuditTrail.class);
        when(studentManager.findStudentById("STU404")).thenReturn(null);
        Path csv = Files.createTempFile("parallel-invalid-", ".csv");
        Files.writeString(csv,
                "studentId,subject,type,grade\n"
                        + "STU404,Math,Core,80\n"
                        + "STU001,Math,Core,not-a-number\n");

        try {
            ImportSummary summary = new BulkImportService(studentManager, gradeManager)
                    .importCSVParallel(csv.toString(), 2, auditTrail);

            assertEquals(2, summary.totalRows());
            assertEquals(0, summary.importedRows());
            assertEquals(2, summary.failedRows());
            verify(auditTrail, times(2))
                    .record(eq("GRADE_IMPORT_FAILURE"), anyString());
            verify(gradeManager, never())
                    .recordGrade(any(Student.class), any(CoreSubject.class), anyDouble());
        } finally {
            Files.deleteIfExists(csv);
        }
    }
}
