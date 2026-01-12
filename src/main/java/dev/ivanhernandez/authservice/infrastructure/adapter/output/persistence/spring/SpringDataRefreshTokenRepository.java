package dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.spring;

import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity.RefreshTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataRefreshTokenRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {

    Optional<RefreshTokenJpaEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE RefreshTokenJpaEntity t SET t.revoked = true WHERE t.tokenHash = :tokenHash")
    void revokeByTokenHash(@Param("tokenHash") String tokenHash);

    @Modifying
    @Query("UPDATE RefreshTokenJpaEntity t SET t.revoked = true WHERE t.userId = :userId")
    void revokeAllByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM RefreshTokenJpaEntity t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}
