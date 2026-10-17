package cache;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Thread-safe bounded LRU cache with optional entry expiry.
 */
public final class ThreadSafeLruCache<K, V> {
    private final int capacity;
    private final long ttlMillis;
    private final Map<K, Entry<V>> entries;

    public ThreadSafeLruCache(int capacity, Duration ttl) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Cache capacity must be positive");
        }
        Objects.requireNonNull(ttl, "TTL cannot be null");
        if (ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("TTL must be positive");
        }
        this.capacity = capacity;
        this.ttlMillis = ttl.toMillis();
        this.entries = new LinkedHashMap<>(capacity, 0.75f, true);
    }

    public synchronized V get(K key) {
        Entry<V> entry = entries.get(key);
        if (entry == null) {
            return null;
        }
        if (entry.expiresAt().isBefore(Instant.now())) {
            entries.remove(key);
            return null;
        }
        return entry.value();
    }

    public synchronized void put(K key, V value) {
        entries.put(key, new Entry<>(value, Instant.now().plusMillis(ttlMillis)));
        while (entries.size() > capacity) {
            entries.remove(entries.keySet().iterator().next());
        }
    }

    public synchronized void invalidate(K key) {
        entries.remove(key);
    }

    public synchronized void clear() {
        entries.clear();
    }

    public synchronized int size() {
        return entries.size();
    }

    private record Entry<V>(V value, Instant expiresAt) {
    }
}
