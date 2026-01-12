package dev.ivanhernandez.authservice.infrastructure.adapter.output.redis;

import dev.ivanhernandez.authservice.application.port.output.RateLimiter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("!prod")
public class InMemoryRateLimiter implements RateLimiter {

    private final Map<String, RateLimitEntry> entries = new ConcurrentHashMap<>();

    @Override
    public boolean isAllowed(String key, int maxAttempts, Duration window) {
        Instant now = Instant.now();

        entries.compute(key, (k, entry) -> {
            if (entry == null || entry.expiresAt.isBefore(now)) {
                return new RateLimitEntry(1, now.plus(window));
            }
            return new RateLimitEntry(entry.count + 1, entry.expiresAt);
        });

        RateLimitEntry entry = entries.get(key);
        return entry != null && entry.count <= maxAttempts;
    }

    @Override
    public long getTimeToReset(String key) {
        RateLimitEntry entry = entries.get(key);
        if (entry == null) {
            return 0;
        }

        long seconds = Duration.between(Instant.now(), entry.expiresAt).toSeconds();
        return Math.max(0, seconds);
    }

    @Override
    public void reset(String key) {
        entries.remove(key);
    }

    public void clearAll() {
        entries.clear();
    }

    private record RateLimitEntry(int count, Instant expiresAt) {
    }
}
