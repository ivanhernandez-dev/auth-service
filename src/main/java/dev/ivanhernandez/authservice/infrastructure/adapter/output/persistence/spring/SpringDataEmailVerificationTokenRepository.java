package dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.spring;

import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity.EmailVerificationTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataEmailVerificationTokenRepository extends JpaRepository<EmailVerificationTokenJpaEntity, UUID> {

    Optional<EmailVerificationTokenJpaEntity> findByToken(String token);

    void deleteByUserId(UUID userId);
}
