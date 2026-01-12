package dev.ivanhernandez.authservice.infrastructure.adapter.output.redis;

import dev.ivanhernandez.authservice.application.port.output.RateLimiter;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@Profile("prod")
public class RedisRateLimiter implements RateLimiter {

    private static final String RATE_LIMIT_PREFIX = "rate_limit:";

    private final StringRedisTemplate redisTemplate;

    public RedisRateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean isAllowed(String key, int maxAttempts, Duration window) {
        String redisKey = RATE_LIMIT_PREFIX + key;

        Long currentCount = redisTemplate.opsForValue().increment(redisKey);

        if (currentCount == null) {
            return true;
        }

        if (currentCount == 1) {
            redisTemplate.expire(redisKey, window.toSeconds(), TimeUnit.SECONDS);
        }

        return currentCount <= maxAttempts;
    }

    @Override
    public long getTimeToReset(String key) {
        String redisKey = RATE_LIMIT_PREFIX + key;
        Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
        return ttl != null && ttl > 0 ? ttl : 0;
    }

    @Override
    public void reset(String key) {
        String redisKey = RATE_LIMIT_PREFIX + key;
        redisTemplate.delete(redisKey);
    }
}
