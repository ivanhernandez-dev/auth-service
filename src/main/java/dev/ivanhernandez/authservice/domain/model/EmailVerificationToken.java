package dev.ivanhernandez.authservice.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class EmailVerificationToken {

    private static final int EXPIRATION_HOURS = 24;

    private UUID id;
    private UUID userId;
    private String token;
    private LocalDateTime expiresAt;
    private boolean used;
    private LocalDateTime createdAt;

    public EmailVerificationToken(UUID id, UUID userId, String token,
                                  LocalDateTime expiresAt, boolean used,
                                  LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.used = used;
        this.createdAt = createdAt;
    }

    public static EmailVerificationToken create(UUID userId, String token) {
        return new EmailVerificationToken(
                null,
                userId,
                token,
                LocalDateTime.now().plusHours(EXPIRATION_HOURS),
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

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !isUsed() && !isExpired();
    }

    public void markAsUsed() {
        this.used = true;
    }
}
