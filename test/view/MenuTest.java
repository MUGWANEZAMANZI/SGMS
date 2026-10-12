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
}
