package services;

import manager.StatisticsCalculator;
import manager.StatisticsResult;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Publishes refreshed class statistics on a background schedule.
 */
public final class StatisticsDashboard implements AutoCloseable {
    private final StatisticsCalculator calculator;
    private final Consumer<StatisticsResult> publisher;
    private final ScheduledExecutorService scheduler;

    public StatisticsDashboard(
            StatisticsCalculator calculator,
            Consumer<StatisticsResult> publisher) {
        this.calculator = Objects.requireNonNull(calculator);
        this.publisher = Objects.requireNonNull(publisher);
        this.scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "statistics-dashboard");
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * Starts dashboard refreshes.
     *
     * @param interval refresh interval
     */
    public void start(Duration interval) {
        Objects.requireNonNull(interval);
        if (interval.isZero() || interval.isNegative()) {
            throw new IllegalArgumentException("Refresh interval must be positive");
        }
        scheduler.scheduleWithFixedDelay(
                this::refreshSafely,
                0,
                interval.toMillis(),
                TimeUnit.MILLISECONDS);
    }

    private void refreshSafely() {
        try {
            publisher.accept(calculator.calculate());
        } catch (RuntimeException exception) {
            System.err.println("Statistics dashboard refresh failed: "
                    + exception.getMessage());
        }
    }

    @Override
    public void close() {
        scheduler.shutdownNow();
    }
}
