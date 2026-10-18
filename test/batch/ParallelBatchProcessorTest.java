package batch;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParallelBatchProcessorTest {

    @Test
    void processesItemsInParallelAndPreservesInputOrder() {
        try (ParallelBatchProcessor<Integer, Integer> processor =
                     new ParallelBatchProcessor<>(3, value -> {
                         if (value == 1) {
                             try {
                                 Thread.sleep(40);
                             } catch (InterruptedException exception) {
                                 Thread.currentThread().interrupt();
                             }
                         }
                         return value * 2;
                     })) {
            assertEquals(List.of(2, 4, 6), processor.process(List.of(1, 2, 3)));
        }
    }

    @Test
    void supportsArrayInput() {
        try (ParallelBatchProcessor<String, Integer> processor =
                     new ParallelBatchProcessor<>(2, String::length)) {
            assertEquals(List.of(1, 2), processor.process(new String[]{"a", "bb"}));
        }
    }

    @Test
    void wrapsOperationFailures() {
        try (ParallelBatchProcessor<Integer, Integer> processor =
                     new ParallelBatchProcessor<>(1, value -> {
                         throw new IllegalArgumentException("bad item");
                     })) {
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> processor.process(List.of(1)));

            assertEquals("Batch item processing failed", exception.getMessage());
            assertEquals(IllegalArgumentException.class,
                    exception.getCause().getClass());
        }
    }

    @Test
    void rejectsInvalidWorkerCount() {
        assertThrows(IllegalArgumentException.class,
                () -> new ParallelBatchProcessor<>(0, value -> value));
    }
}
