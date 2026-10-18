package cache;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ThreadSafeLruCacheTest {

    @Test
    void evictsLeastRecentlyUsedEntryWhenCapacityIsExceeded() {
        ThreadSafeLruCache<String, Integer> cache =
                new ThreadSafeLruCache<>(2, Duration.ofMinutes(1));

        cache.put("first", 1);
        cache.put("second", 2);
        assertEquals(1, cache.get("first"));
        cache.put("third", 3);

        assertNull(cache.get("second"));
        assertEquals(1, cache.get("first"));
        assertEquals(3, cache.get("third"));
        assertEquals(2, cache.size());
    }

    @Test
    void expiresEntriesAfterConfiguredTtl() throws InterruptedException {
        ThreadSafeLruCache<String, String> cache =
                new ThreadSafeLruCache<>(1, Duration.ofMillis(20));

        cache.put("key", "value");
        Thread.sleep(50);

        assertNull(cache.get("key"));
        assertEquals(0, cache.size());
    }

    @Test
    void supportsInvalidationAndClear() {
        ThreadSafeLruCache<String, Integer> cache =
                new ThreadSafeLruCache<>(2, Duration.ofMinutes(1));
        cache.put("one", 1);
        cache.put("two", 2);

        cache.invalidate("one");
        assertNull(cache.get("one"));
        assertEquals(1, cache.size());

        cache.clear();
        assertEquals(0, cache.size());
    }

    @Test
    void rejectsInvalidConfiguration() {
        assertThrows(IllegalArgumentException.class,
                () -> new ThreadSafeLruCache<>(0, Duration.ofMinutes(1)));
        assertThrows(IllegalArgumentException.class,
                () -> new ThreadSafeLruCache<>(1, Duration.ZERO));
    }
}
