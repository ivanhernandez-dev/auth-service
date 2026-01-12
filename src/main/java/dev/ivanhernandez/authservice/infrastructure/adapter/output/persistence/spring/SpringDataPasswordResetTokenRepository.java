package dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.spring;

import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity.PasswordResetTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataPasswordResetTokenRepository extends JpaRepository<PasswordResetTokenJpaEntity, UUID> {

    Optional<PasswordResetTokenJpaEntity> findByToken(String token);

    void deleteByUserId(UUID userId);
}
