package dev.ivanhernandez.authservice.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class RefreshToken {

    private static final int EXPIRATION_DAYS = 30;

    private UUID id;
    private UUID userId;
    private String tokenHash;
    private LocalDateTime expiresAt;
    private boolean revoked;
    private LocalDateTime createdAt;

    public RefreshToken(UUID id, UUID userId, String tokenHash,
                        LocalDateTime expiresAt, boolean revoked,
                        LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.createdAt = createdAt;
    }

    public static RefreshToken create(UUID userId, String tokenHash) {
        return new RefreshToken(
                null,
                userId,
                tokenHash,
                LocalDateTime.now().plusDays(EXPIRATION_DAYS),
                false,
                LocalDateTime.now()
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !isRevoked() && !isExpired();
    }

    public void revoke() {
        this.revoked = true;
    }
}
