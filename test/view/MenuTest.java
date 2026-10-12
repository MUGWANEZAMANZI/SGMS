package view;

import app.ApplicationActions;
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

    @Test
    void delegatesGradeReportActionToInjectedApplicationActions() {
        ApplicationActions actions = mock(ApplicationActions.class);
        Menu menu = new Menu(
                mock(StudentApplicationService.class),
                mock(GradeService.class),
                mock(StudentSearchService.class),
                new BulkImportService(new StudentManager(), new GradeManager()),
                mock(InputReader.class),
                mock(StatisticsView.class),
                actions);

        menu.handleOptions(4);

        verify(actions).viewGradeReport();
    }

    @Test
    void delegatesEveryApplicationActionAndReturnsFalseOnExit() {
        ApplicationActions actions = mock(ApplicationActions.class);
        StudentApplicationService students = mock(StudentApplicationService.class);
        GradeService grades = mock(GradeService.class);
        StudentSearchService search = mock(StudentSearchService.class);
        StatisticsView statistics = mock(StatisticsView.class);
        Menu menu = new Menu(students, grades, search,
                new BulkImportService(new StudentManager(), new GradeManager()),
                mock(InputReader.class), statistics, actions);

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
        verify(actions).viewGradeReport();
        verify(actions).exportGradeReport();
        verify(actions).calculateStudentGPA();
        verify(statistics).displayStatistics();
        verify(search).search();
    }

    @Test
    void catchesInputAndImportFailuresWithoutStoppingMenu() {
        ApplicationActions actions = mock(ApplicationActions.class);
        doThrow(new exceptions.InvalidInputException("bad input"))
                .when(actions).calculateStudentGPA();
        Menu menu = new Menu(mock(StudentApplicationService.class),
                mock(GradeService.class), mock(StudentSearchService.class),
                new BulkImportService(new StudentManager(), new GradeManager()),
                mock(InputReader.class), mock(StatisticsView.class), actions);

        assertTrue(menu.handleOptions(6));
    }
}
