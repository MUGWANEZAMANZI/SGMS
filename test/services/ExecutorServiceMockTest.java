package services;

import grading.GPACalculator;
import manager.GradeManager;
import manager.StudentManager;
import model.CoreSubject;
import model.RegularStudent;
import model.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import report.GradeReport;
import report.GradeReportService;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Integration tests that mock ExecutorService dependencies to verify
 * concurrent service wiring without relying on thread-scheduling timing.
 */
@ExtendWith(MockitoExtension.class)
class ExecutorServiceMockTest {

    @Mock
    private GradeReportService reportService;

    @Mock
    private GradeNotification notification;

    // -------------------------------------------------------------------------
    // ConcurrentReportService — FixedThreadPool (mocked GradeReportService)
    // -------------------------------------------------------------------------

    @Test
    void concurrentReportServiceCallsReportServiceOncePerStudent() {
        GradeReport stub = mock(GradeReport.class);
        when(reportService.createReport(anyString())).thenReturn(stub);

        Student alice = new RegularStudent("Alice", 20, "alice@test.com", "0712345678");
        Student bob   = new RegularStudent("Bob",   21, "bob@test.com",   "0712345679");

        try (ConcurrentReportService service = new ConcurrentReportService(reportService, 2)) {
            List<GradeReport> reports = service.generate(new Student[]{alice, bob});
            assertEquals(2, reports.size());
            verify(reportService, times(2)).createReport(anyString());
        }
    }

    @Test
    void concurrentReportServicePreservesInputOrder() {
        GradeReport r1 = mock(GradeReport.class);
        GradeReport r2 = mock(GradeReport.class);
        Student alice = new RegularStudent("Alice", 20, "alice@test.com", "0712345678");
        Student bob   = new RegularStudent("Bob",   21, "bob@test.com",   "0712345679");

        when(reportService.createReport(alice.getStudentId())).thenReturn(r1);
        when(reportService.createReport(bob.getStudentId())).thenReturn(r2);

        try (ConcurrentReportService service = new ConcurrentReportService(reportService, 2)) {
            List<GradeReport> reports = service.generate(new Student[]{alice, bob});
            assertSame(r1, reports.get(0), "First report must correspond to first student");
            assertSame(r2, reports.get(1), "Second report must correspond to second student");
        }
    }

    @Test
    void concurrentReportServiceHandlesEmptyInput() {
        try (ConcurrentReportService service = new ConcurrentReportService(reportService, 2)) {
            List<GradeReport> reports = service.generate(new Student[0]);
            assertTrue(reports.isEmpty());
            verifyNoInteractions(reportService);
        }
    }

    @Test
    void concurrentReportServiceRejectsZeroWorkerCount() {
        assertThrows(IllegalArgumentException.class,
                () -> new ConcurrentReportService(reportService, 0));
    }

    // -------------------------------------------------------------------------
    // ParallelBatchProcessor — FixedThreadPool with mocked operation
    // -------------------------------------------------------------------------

    @Test
    void parallelBatchProcessorMapsEachInputToOutput() {
        try (batch.ParallelBatchProcessor<Integer, Integer> processor =
                     new batch.ParallelBatchProcessor<>(3, x -> x * 10)) {
            List<Integer> results = processor.process(List.of(1, 2, 3, 4, 5));
            assertEquals(List.of(10, 20, 30, 40, 50), results);
        }
    }

    @Test
    void parallelBatchProcessorPreservesOrderAcrossThreads() {
        try (batch.ParallelBatchProcessor<Integer, String> processor =
                     new batch.ParallelBatchProcessor<>(4, i -> "item-" + i)) {
            List<String> results = processor.process(List.of(1, 2, 3));
            assertEquals(List.of("item-1", "item-2", "item-3"), results);
        }
    }

    // -------------------------------------------------------------------------
    // ScheduledGpaProcessor — ScheduledThreadPool (mocked notification)
    // -------------------------------------------------------------------------

    @Test
    void scheduledGpaProcessorStartsAndClosesWithoutError() {
        StudentManager sm = new StudentManager();
        RankingService rs = new RankingService(new GradeManager(), new GPACalculator());

        assertDoesNotThrow(() -> {
            try (ScheduledGpaProcessor processor = new ScheduledGpaProcessor(sm, rs, notification)) {
                processor.start(Duration.ofSeconds(30));
            }
        });
        verifyNoInteractions(notification); // no students → no notification fired
    }

    @Test
    void scheduledGpaProcessorRejectsZeroInterval() {
        StudentManager sm = new StudentManager();
        RankingService rs = new RankingService(new GradeManager(), new GPACalculator());

        try (ScheduledGpaProcessor processor = new ScheduledGpaProcessor(sm, rs, notification)) {
            assertThrows(IllegalArgumentException.class,
                    () -> processor.start(Duration.ZERO));
        }
    }

    // -------------------------------------------------------------------------
    // StatisticsDashboard — ScheduledThreadPool (mocked publisher)
    // -------------------------------------------------------------------------

    @Test
    void statisticsDashboardStartsAndReturnsLatestResult() {
        StudentManager sm = new StudentManager();
        GradeManager gm = new GradeManager();
        manager.StatisticsCalculator calc = new manager.StatisticsCalculator(sm, gm);

        try (StatisticsDashboard dashboard = new StatisticsDashboard(calc, result -> {})) {
            dashboard.start(Duration.ofSeconds(30));
            assertNotNull(dashboard.getLatest());
        }
    }
}
