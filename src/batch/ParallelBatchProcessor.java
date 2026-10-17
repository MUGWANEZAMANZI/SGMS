package batch;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * Processes independent inputs in parallel while preserving input order.
 */
public final class ParallelBatchProcessor<I, O> implements AutoCloseable {
    private final ExecutorService workers;
    private final Function<I, O> operation;

    public ParallelBatchProcessor(int workerCount, Function<I, O> operation) {
        if (workerCount < 1) {
            throw new IllegalArgumentException("Worker count must be positive");
        }
        this.operation = Objects.requireNonNull(operation);
        this.workers = Executors.newFixedThreadPool(workerCount);
    }

    public List<O> process(List<I> inputs) {
        Objects.requireNonNull(inputs);
        var futures = inputs.stream()
                .map(input -> workers.submit(() -> operation.apply(input)))
                .toList();
        return futures.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Batch processing interrupted", exception);
            } catch (ExecutionException exception) {
                throw new IllegalStateException(
                        "Batch item processing failed", exception.getCause());
            }
        }).toList();
    }

    public List<O> process(I[] inputs) {
        Objects.requireNonNull(inputs);
        return process(Arrays.asList(inputs));
    }

    @Override
    public void close() {
        workers.shutdown();
    }
}
