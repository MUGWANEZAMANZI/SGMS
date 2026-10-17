package services;

import report.GradeReport;
import report.GradeReportService;
import model.Student;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Generates reports concurrently using a bounded worker pool.
 */
public final class ConcurrentReportService implements AutoCloseable {
    private final GradeReportService reportService;
    private final ExecutorService workers;

    public ConcurrentReportService(GradeReportService reportService, int workerCount) {
        this.reportService = Objects.requireNonNull(reportService);
        if (workerCount < 1) {
            throw new IllegalArgumentException("Worker count must be positive");
        }
        this.workers = Executors.newFixedThreadPool(workerCount, runnable -> {
            Thread thread = new Thread(runnable, "report-worker");
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * Generates one report per student in parallel.
     *
     * @param students students to process
     * @return reports in the same order as the input
     */
    public List<GradeReport> generate(Student[] students) {
        Objects.requireNonNull(students);
        try {
            var tasks = Arrays.stream(students)
                    .map(student -> workers.submit(
                            () -> reportService.createReport(student.getStudentId())))
                    .toList();
            return tasks.stream().map(future -> {
                try {
                    return future.get();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Report generation interrupted", exception);
                } catch (ExecutionException exception) {
                    throw new IllegalStateException("Report generation failed", exception.getCause());
                }
            }).toList();
        } finally {
            // Individual tasks are completed or cancelled by the caller through close().
        }
    }

    @Override
    public void close() {
        workers.shutdown();
    }
}
