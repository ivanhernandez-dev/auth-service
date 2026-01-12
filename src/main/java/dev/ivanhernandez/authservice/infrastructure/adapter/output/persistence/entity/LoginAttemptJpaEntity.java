package dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity;

import dev.ivanhernandez.authservice.domain.model.LoginAttempt;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "login_attempts")
public class LoginAttemptJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    private String email;

    @Column(name = "tenant_slug", nullable = false, length = 50)
    private String tenantSlug;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(nullable = false)
    private boolean success;

    @Column(name = "attempted_at", nullable = false)
    private LocalDateTime attemptedAt;

    public LoginAttemptJpaEntity() {
    }

    public static LoginAttemptJpaEntity fromDomain(LoginAttempt attempt) {
        LoginAttemptJpaEntity entity = new LoginAttemptJpaEntity();
        entity.id = attempt.getId();
        entity.userId = attempt.getUserId();
        entity.email = attempt.getEmail();
        entity.tenantSlug = attempt.getTenantSlug();
        entity.ipAddress = attempt.getIpAddress();
        entity.userAgent = attempt.getUserAgent();
        entity.success = attempt.isSuccess();
        entity.attemptedAt = attempt.getAttemptedAt();
        return entity;
    }

    public LoginAttempt toDomain() {
        return new LoginAttempt(id, userId, email, tenantSlug, ipAddress, userAgent, success, attemptedAt);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTenantSlug() {
        return tenantSlug;
    }

    public void setTenantSlug(String tenantSlug) {
        this.tenantSlug = tenantSlug;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(LocalDateTime attemptedAt) {
        this.attemptedAt = attemptedAt;
    }
}
