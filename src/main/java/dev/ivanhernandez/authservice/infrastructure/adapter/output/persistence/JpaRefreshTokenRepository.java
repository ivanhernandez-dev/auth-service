package dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence;

import dev.ivanhernandez.authservice.application.port.output.RefreshTokenRepository;
import dev.ivanhernandez.authservice.domain.model.RefreshToken;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity.RefreshTokenJpaEntity;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.spring.SpringDataRefreshTokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRefreshTokenRepository implements RefreshTokenRepository {

    private final SpringDataRefreshTokenRepository springDataRepository;

    public JpaRefreshTokenRepository(SpringDataRefreshTokenRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        RefreshTokenJpaEntity entity = RefreshTokenJpaEntity.fromDomain(token);
        RefreshTokenJpaEntity saved = springDataRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return springDataRepository.findByTokenHash(tokenHash)
                .map(RefreshTokenJpaEntity::toDomain);
    }

    @Override
    @Transactional
    public void revokeByTokenHash(String tokenHash) {
        springDataRepository.revokeByTokenHash(tokenHash);
    }

    @Override
    @Transactional
    public void revokeAllByUserId(UUID userId) {
        springDataRepository.revokeAllByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        springDataRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
