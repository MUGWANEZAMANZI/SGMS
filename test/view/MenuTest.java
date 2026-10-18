package view;

import input.InputReader;
import manager.GradeManager;
import manager.StudentManager;
import org.junit.jupiter.api.Test;
import services.BulkImportService;
import services.GradeService;
import services.StudentApplicationService;
import services.StudentSearchService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuTest {

    private Menu buildMenu(
            StudentApplicationService students,
            GradeService grades,
            StudentSearchService search,
            StatisticsView statistics,
            GradeReportView gradeReport,
            ExportReportView exportReport,
            GPAView gpa) {
        return new Menu(students, grades, search,
                new BulkImportService(new StudentManager(), new GradeManager()),
                mock(InputReader.class), statistics,
                gradeReport, exportReport, gpa);
    }

    @Test
    void delegatesGradeReportViewOnOptionFour() {
        GradeReportView gradeReportView = mock(GradeReportView.class);
        Menu menu = buildMenu(
                mock(StudentApplicationService.class),
                mock(GradeService.class),
                mock(StudentSearchService.class),
                mock(StatisticsView.class),
                gradeReportView,
                mock(ExportReportView.class),
                mock(GPAView.class));

        menu.handleOptions(4);

        verify(gradeReportView).display();
    }

    @Test
    void delegatesEveryViewAndReturnsFalseOnExit() {
        StudentApplicationService students = mock(StudentApplicationService.class);
        GradeService grades = mock(GradeService.class);
        StudentSearchService search = mock(StudentSearchService.class);
        StatisticsView statistics = mock(StatisticsView.class);
        GradeReportView gradeReport = mock(GradeReportView.class);
        ExportReportView exportReport = mock(ExportReportView.class);
        GPAView gpa = mock(GPAView.class);
        Menu menu = buildMenu(students, grades, search, statistics, gradeReport, exportReport, gpa);

        assertTrue(menu.handleOptions(1));
        assertTrue(menu.handleOptions(2));
        assertTrue(menu.handleOptions(3));
        assertTrue(menu.handleOptions(4));
        assertTrue(menu.handleOptions(5));
        assertTrue(menu.handleOptions(6));
        assertTrue(menu.handleOptions(8));
        assertTrue(menu.handleOptions(9));
        assertTrue(menu.handleOptions(99));
        assertFalse(menu.handleOptions(10));

        verify(students).addStudent();
        verify(students).viewStudents();
        verify(grades).recordGrade();
        verify(gradeReport).display();
        verify(exportReport).export();
        verify(gpa).displayStudentGPA();
        verify(statistics).displayStatistics();
        verify(search).search();
    }

    @Test
    void catchesInputFailuresWithoutStoppingMenu() {
        ExportReportView exportReport = mock(ExportReportView.class);
        doThrow(new exceptions.InvalidInputException("bad input"))
                .when(exportReport).export();
        Menu menu = buildMenu(
                mock(StudentApplicationService.class),
                mock(GradeService.class),
                mock(StudentSearchService.class),
                mock(StatisticsView.class),
                mock(GradeReportView.class),
                exportReport,
                mock(GPAView.class));

        assertTrue(menu.handleOptions(5));
    }
}
