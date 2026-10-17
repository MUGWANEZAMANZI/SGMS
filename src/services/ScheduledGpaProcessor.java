package services;

import manager.StudentManager;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Recalculates student GPA values on a schedule and reports changes.
 */
public final class ScheduledGpaProcessor implements AutoCloseable {
    private final StudentManager studentManager;
    private final RankingService rankingService;
    private final GradeNotification notification;
    private final ScheduledExecutorService scheduler;
    private final Map<String, Double> lastGpa = new ConcurrentHashMap<>();

    public ScheduledGpaProcessor(
            StudentManager studentManager,
            RankingService rankingService,
            GradeNotification notification) {
        this.studentManager = Objects.requireNonNull(studentManager);
        this.rankingService = Objects.requireNonNull(rankingService);
        this.notification = Objects.requireNonNull(notification);
        this.scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "scheduled-gpa-processor");
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * Starts periodic GPA processing.
     *
     * @param interval update interval
     */
    public void start(Duration interval) {
        Objects.requireNonNull(interval);
        if (interval.isZero() || interval.isNegative()) {
            throw new IllegalArgumentException("Update interval must be positive");
        }
        scheduler.scheduleWithFixedDelay(
                this::processSafely,
                0,
                interval.toMillis(),
                TimeUnit.MILLISECONDS);
    }

    private void processSafely() {
        try {
            for (var student : studentManager.getStudents()) {
                StudentGPAResult result = rankingService.calculateStudentGPA(student);
                Double previous = lastGpa.put(student.getStudentId(), result.cumulativeGpa());
                if (previous == null || Double.compare(previous, result.cumulativeGpa()) != 0) {
                    notification.notify(result);
                }
            }
        } catch (RuntimeException exception) {
            System.err.println("Scheduled GPA processing failed: " + exception.getMessage());
        }
    }

    @Override
    public void close() {
        scheduler.shutdownNow();
    }
}
