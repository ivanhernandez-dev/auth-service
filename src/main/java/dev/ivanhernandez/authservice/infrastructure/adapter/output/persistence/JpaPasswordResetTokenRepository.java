package dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence;

import dev.ivanhernandez.authservice.application.port.output.PasswordResetTokenRepository;
import dev.ivanhernandez.authservice.domain.model.PasswordResetToken;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity.PasswordResetTokenJpaEntity;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.spring.SpringDataPasswordResetTokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaPasswordResetTokenRepository implements PasswordResetTokenRepository {

    private final SpringDataPasswordResetTokenRepository springDataRepository;

    public JpaPasswordResetTokenRepository(SpringDataPasswordResetTokenRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        PasswordResetTokenJpaEntity entity = PasswordResetTokenJpaEntity.fromDomain(token);
        PasswordResetTokenJpaEntity saved = springDataRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return springDataRepository.findByToken(token)
                .map(PasswordResetTokenJpaEntity::toDomain);
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        springDataRepository.deleteByUserId(userId);
    }
}
