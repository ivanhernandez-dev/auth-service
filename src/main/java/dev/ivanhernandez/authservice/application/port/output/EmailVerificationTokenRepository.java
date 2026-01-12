package dev.ivanhernandez.authservice.application.port.output;

import dev.ivanhernandez.authservice.domain.model.EmailVerificationToken;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationTokenRepository {

    EmailVerificationToken save(EmailVerificationToken token);

    Optional<EmailVerificationToken> findByToken(String token);

    void deleteByUserId(UUID userId);
}
