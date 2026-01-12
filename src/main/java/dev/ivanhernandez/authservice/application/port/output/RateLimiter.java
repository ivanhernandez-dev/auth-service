package dev.ivanhernandez.authservice.application.port.output;

import java.time.Duration;

public interface RateLimiter {

    boolean isAllowed(String key, int maxAttempts, Duration window);

    long getTimeToReset(String key);

    void reset(String key);
}
