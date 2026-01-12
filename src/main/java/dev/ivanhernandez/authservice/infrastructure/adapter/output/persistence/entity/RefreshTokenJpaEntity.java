package dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity;

import dev.ivanhernandez.authservice.domain.model.RefreshToken;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public RefreshTokenJpaEntity() {
    }

    public static RefreshTokenJpaEntity fromDomain(RefreshToken token) {
        RefreshTokenJpaEntity entity = new RefreshTokenJpaEntity();
        entity.id = token.getId();
        entity.userId = token.getUserId();
        entity.tokenHash = token.getTokenHash();
        entity.expiresAt = token.getExpiresAt();
        entity.revoked = token.isRevoked();
        entity.createdAt = token.getCreatedAt();
        return entity;
    }

    public RefreshToken toDomain() {
        return new RefreshToken(id, userId, tokenHash, expiresAt, revoked, createdAt);
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

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
