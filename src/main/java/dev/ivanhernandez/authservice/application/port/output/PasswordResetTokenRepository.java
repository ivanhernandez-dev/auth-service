package dev.ivanhernandez.authservice.application.port.output;

import dev.ivanhernandez.authservice.domain.model.PasswordResetToken;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository {

    PasswordResetToken save(PasswordResetToken token);

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUserId(UUID userId);
}
