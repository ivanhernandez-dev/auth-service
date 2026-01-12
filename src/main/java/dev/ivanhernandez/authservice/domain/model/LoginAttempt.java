package dev.ivanhernandez.authservice.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class LoginAttempt {

    private UUID id;
    private UUID userId;
    private String email;
    private String tenantSlug;
    private String ipAddress;
    private String userAgent;
    private boolean success;
    private LocalDateTime attemptedAt;

    public LoginAttempt(UUID id, UUID userId, String email, String tenantSlug,
                        String ipAddress, String userAgent, boolean success,
                        LocalDateTime attemptedAt) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.tenantSlug = tenantSlug;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.success = success;
        this.attemptedAt = attemptedAt;
    }

    public static LoginAttempt success(UUID userId, String email, String tenantSlug,
                                       String ipAddress, String userAgent) {
        return new LoginAttempt(
                null,
                userId,
                email,
                tenantSlug,
                ipAddress,
                userAgent,
                true,
                LocalDateTime.now()
        );
    }

    public static LoginAttempt failure(String email, String tenantSlug,
                                       String ipAddress, String userAgent) {
        return new LoginAttempt(
                null,
                null,
                email,
                tenantSlug,
                ipAddress,
                userAgent,
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

    public String getEmail() {
        return email;
    }

    public String getTenantSlug() {
        return tenantSlug;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public boolean isSuccess() {
        return success;
    }

    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }
}
