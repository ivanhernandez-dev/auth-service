package dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.spring;

import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity.LoginAttemptJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SpringDataLoginAttemptRepository extends JpaRepository<LoginAttemptJpaEntity, UUID> {

    @Query("SELECT COUNT(a) FROM LoginAttemptJpaEntity a WHERE a.email = :email AND a.tenantSlug = :tenantSlug AND a.success = false AND a.attemptedAt > :since")
    long countFailedAttemptsByEmailSince(@Param("email") String email, @Param("tenantSlug") String tenantSlug, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(a) FROM LoginAttemptJpaEntity a WHERE a.ipAddress = :ipAddress AND a.success = false AND a.attemptedAt > :since")
    long countFailedAttemptsByIpSince(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);
}
