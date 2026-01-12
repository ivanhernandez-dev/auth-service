package dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity;

import dev.ivanhernandez.authservice.domain.model.PasswordResetToken;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetTokenJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public PasswordResetTokenJpaEntity() {
    }

    public static PasswordResetTokenJpaEntity fromDomain(PasswordResetToken token) {
        PasswordResetTokenJpaEntity entity = new PasswordResetTokenJpaEntity();
        entity.id = token.getId();
        entity.userId = token.getUserId();
        entity.token = token.getToken();
        entity.expiresAt = token.getExpiresAt();
        entity.used = token.isUsed();
        entity.createdAt = token.getCreatedAt();
        return entity;
    }

    public PasswordResetToken toDomain() {
        return new PasswordResetToken(id, userId, token, expiresAt, used, createdAt);
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
