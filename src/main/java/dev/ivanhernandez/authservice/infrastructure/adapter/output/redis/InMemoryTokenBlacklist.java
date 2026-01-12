package dev.ivanhernandez.authservice.infrastructure.adapter.output.redis;

import dev.ivanhernandez.authservice.application.port.output.TokenBlacklist;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Profile("!prod")
public class InMemoryTokenBlacklist implements TokenBlacklist {

    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    @Override
    public void blacklist(String tokenId, long expirationSeconds) {
        Instant expiresAt = Instant.now().plusSeconds(expirationSeconds);
        blacklist.put(tokenId, expiresAt);
        cleanupExpired();
    }

    @Override
    public boolean isBlacklisted(String tokenId) {
        Instant expiresAt = blacklist.get(tokenId);
        if (expiresAt == null) {
            return false;
        }
        if (expiresAt.isBefore(Instant.now())) {
            blacklist.remove(tokenId);
            return false;
        }
        return true;
    }

    private void cleanupExpired() {
        Instant now = Instant.now();
        blacklist.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}
