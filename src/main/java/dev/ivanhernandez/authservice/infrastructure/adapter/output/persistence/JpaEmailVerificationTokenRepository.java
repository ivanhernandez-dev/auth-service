package dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence;

import dev.ivanhernandez.authservice.application.port.output.EmailVerificationTokenRepository;
import dev.ivanhernandez.authservice.domain.model.EmailVerificationToken;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity.EmailVerificationTokenJpaEntity;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.spring.SpringDataEmailVerificationTokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaEmailVerificationTokenRepository implements EmailVerificationTokenRepository {

    private final SpringDataEmailVerificationTokenRepository springDataRepository;

    public JpaEmailVerificationTokenRepository(SpringDataEmailVerificationTokenRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public EmailVerificationToken save(EmailVerificationToken token) {
        EmailVerificationTokenJpaEntity entity = EmailVerificationTokenJpaEntity.fromDomain(token);
        EmailVerificationTokenJpaEntity saved = springDataRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<EmailVerificationToken> findByToken(String token) {
        return springDataRepository.findByToken(token)
                .map(EmailVerificationTokenJpaEntity::toDomain);
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        springDataRepository.deleteByUserId(userId);
    }
}
