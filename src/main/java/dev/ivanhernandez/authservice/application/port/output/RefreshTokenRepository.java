package dev.ivanhernandez.authservice.application.port.output;

import dev.ivanhernandez.authservice.domain.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken token);

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void revokeByTokenHash(String tokenHash);

    void revokeAllByUserId(UUID userId);

    void deleteExpiredTokens();
}
