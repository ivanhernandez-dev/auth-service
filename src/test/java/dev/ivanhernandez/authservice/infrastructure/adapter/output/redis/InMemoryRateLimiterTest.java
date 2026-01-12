package dev.ivanhernandez.authservice.infrastructure.adapter.output.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryRateLimiterTest {

    private InMemoryRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new InMemoryRateLimiter();
    }

    @Test
    @DisplayName("isAllowed should return true when under limit")
    void isAllowed_shouldReturnTrue_whenUnderLimit() {
        String key = "test-key";
        int maxAttempts = 5;
        Duration window = Duration.ofMinutes(15);

        for (int i = 0; i < maxAttempts; i++) {
            assertTrue(rateLimiter.isAllowed(key, maxAttempts, window));
        }
    }

    @Test
    @DisplayName("isAllowed should return false when limit exceeded")
    void isAllowed_shouldReturnFalse_whenLimitExceeded() {
        String key = "test-key-limit";
        int maxAttempts = 3;
        Duration window = Duration.ofMinutes(15);

        for (int i = 0; i < maxAttempts; i++) {
            rateLimiter.isAllowed(key, maxAttempts, window);
        }

        assertFalse(rateLimiter.isAllowed(key, maxAttempts, window));
    }

    @Test
    @DisplayName("reset should clear the counter")
    void reset_shouldClearTheCounter() {
        String key = "test-key-reset";
        int maxAttempts = 2;
        Duration window = Duration.ofMinutes(15);

        rateLimiter.isAllowed(key, maxAttempts, window);
        rateLimiter.isAllowed(key, maxAttempts, window);
        assertFalse(rateLimiter.isAllowed(key, maxAttempts, window));

        rateLimiter.reset(key);

        assertTrue(rateLimiter.isAllowed(key, maxAttempts, window));
    }

    @Test
    @DisplayName("getTimeToReset should return remaining time")
    void getTimeToReset_shouldReturnRemainingTime() {
        String key = "test-key-time";
        int maxAttempts = 5;
        Duration window = Duration.ofMinutes(15);

        rateLimiter.isAllowed(key, maxAttempts, window);

        long timeToReset = rateLimiter.getTimeToReset(key);

        assertTrue(timeToReset > 0);
        assertTrue(timeToReset <= 900);
    }

    @Test
    @DisplayName("getTimeToReset should return 0 for unknown key")
    void getTimeToReset_shouldReturnZero_forUnknownKey() {
        long timeToReset = rateLimiter.getTimeToReset("unknown-key");

        assertEquals(0, timeToReset);
    }

    @Test
    @DisplayName("different keys should have independent counters")
    void differentKeys_shouldHaveIndependentCounters() {
        String key1 = "key-1";
        String key2 = "key-2";
        int maxAttempts = 2;
        Duration window = Duration.ofMinutes(15);

        rateLimiter.isAllowed(key1, maxAttempts, window);
        rateLimiter.isAllowed(key1, maxAttempts, window);

        assertTrue(rateLimiter.isAllowed(key2, maxAttempts, window));
        assertFalse(rateLimiter.isAllowed(key1, maxAttempts, window));
    }
}
