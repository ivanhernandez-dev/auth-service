package dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence;

import dev.ivanhernandez.authservice.application.port.output.LoginAttemptRepository;
import dev.ivanhernandez.authservice.domain.model.LoginAttempt;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity.LoginAttemptJpaEntity;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.spring.SpringDataLoginAttemptRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class JpaLoginAttemptRepository implements LoginAttemptRepository {

    private final SpringDataLoginAttemptRepository springDataRepository;

    public JpaLoginAttemptRepository(SpringDataLoginAttemptRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public LoginAttempt save(LoginAttempt attempt) {
        LoginAttemptJpaEntity entity = LoginAttemptJpaEntity.fromDomain(attempt);
        LoginAttemptJpaEntity saved = springDataRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public long countFailedAttemptsByEmailSince(String email, String tenantSlug, LocalDateTime since) {
        return springDataRepository.countFailedAttemptsByEmailSince(email, tenantSlug, since);
    }

    @Override
    public long countFailedAttemptsByIpSince(String ipAddress, LocalDateTime since) {
        return springDataRepository.countFailedAttemptsByIpSince(ipAddress, since);
    }
}
