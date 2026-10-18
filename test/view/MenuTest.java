package view;

import exceptions.InvalidInputException;
import exceptions.StudentNotFoundException;
import input.InputReader;
import manager.GradeManager;
import manager.StudentManager;
import org.junit.jupiter.api.Test;
import report.GradeReportFileService;
import report.StudentImportService;
import services.BulkImportService;
import services.ConcurrentReportService;
import services.GradeService;
import services.StudentApplicationService;
import services.StudentSearchService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class MenuTest {

    private StudentManager sharedManager = new StudentManager();

    private Menu buildMenu(
            StudentApplicationService students,
            GradeService grades,
            StudentSearchService search,
            StatisticsView statistics,
            GradeReportView gradeReport,
            ExportReportView exportReport,
            GPAView gpa) {
        InputReader reader = mock(InputReader.class);
        ConcurrentReportService concurrent = mock(ConcurrentReportService.class);
        when(concurrent.generate(any())).thenReturn(java.util.List.of());
        return new Menu(students, grades, search,
                new BulkImportService(sharedManager, new GradeManager()),
                reader, statistics, gradeReport, exportReport, gpa,
                null,
                concurrent,
                new GradeReportFileService(),
                sharedManager);
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
        doThrow(new InvalidInputException("bad input")).when(exportReport).export();
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

    @Test
    void option14DelegatesEditStudent() {
        StudentApplicationService students = mock(StudentApplicationService.class);
        Menu menu = buildMenu(students,
                mock(GradeService.class), mock(StudentSearchService.class),
                mock(StatisticsView.class), mock(GradeReportView.class),
                mock(ExportReportView.class), mock(GPAView.class));

        assertTrue(menu.handleOptions(14));
        verify(students).editStudent();
    }

    @Test
    void option15DelegatesDeactivateStudent() {
        StudentApplicationService students = mock(StudentApplicationService.class);
        Menu menu = buildMenu(students,
                mock(GradeService.class), mock(StudentSearchService.class),
                mock(StatisticsView.class), mock(GradeReportView.class),
                mock(ExportReportView.class), mock(GPAView.class));

        assertTrue(menu.handleOptions(15));
        verify(students).deactivateStudent();
    }

    @Test
    void option16DelegatesActivateStudent() {
        StudentApplicationService students = mock(StudentApplicationService.class);
        Menu menu = buildMenu(students,
                mock(GradeService.class), mock(StudentSearchService.class),
                mock(StatisticsView.class), mock(GradeReportView.class),
                mock(ExportReportView.class), mock(GPAView.class));

        assertTrue(menu.handleOptions(16));
        verify(students).activateStudent();
    }

    @Test
    void catchesStudentNotFoundWithoutStoppingMenu() {
        StudentApplicationService students = mock(StudentApplicationService.class);
        doThrow(new StudentNotFoundException("STU999")).when(students).editStudent();
        Menu menu = buildMenu(students,
                mock(GradeService.class), mock(StudentSearchService.class),
                mock(StatisticsView.class), mock(GradeReportView.class),
                mock(ExportReportView.class), mock(GPAView.class));

        assertTrue(menu.handleOptions(14));
    }

    @Test
    void option12BatchReportsShowsNoneWhenNoStudents() {
        Menu menu = buildMenu(
                mock(StudentApplicationService.class),
                mock(GradeService.class), mock(StudentSearchService.class),
                mock(StatisticsView.class), mock(GradeReportView.class),
                mock(ExportReportView.class), mock(GPAView.class));

        assertTrue(menu.handleOptions(12));
    }

    @Test
    void option11StudentImportNotConfiguredReturnsTrue() {
        Menu menu = buildMenu(
                mock(StudentApplicationService.class),
                mock(GradeService.class), mock(StudentSearchService.class),
                mock(StatisticsView.class), mock(GradeReportView.class),
                mock(ExportReportView.class), mock(GPAView.class));

        assertTrue(menu.handleOptions(11));
    }
}
