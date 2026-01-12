package dev.ivanhernandez.authservice.infrastructure.adapter.output.redis;

import dev.ivanhernandez.authservice.application.port.output.TokenBlacklist;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Profile("prod")
public class RedisTokenBlacklist implements TokenBlacklist {

    private static final String BLACKLIST_PREFIX = "token_blacklist:";

    private final StringRedisTemplate redisTemplate;

    public RedisTokenBlacklist(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void blacklist(String tokenId, long expirationSeconds) {
        String key = BLACKLIST_PREFIX + tokenId;
        redisTemplate.opsForValue().set(key, "1", expirationSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean isBlacklisted(String tokenId) {
        String key = BLACKLIST_PREFIX + tokenId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
