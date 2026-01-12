package dev.ivanhernandez.authservice.application.port.output;

import dev.ivanhernandez.authservice.domain.model.LoginAttempt;

import java.time.LocalDateTime;

public interface LoginAttemptRepository {

    LoginAttempt save(LoginAttempt attempt);

    long countFailedAttemptsByEmailSince(String email, String tenantSlug, LocalDateTime since);

    long countFailedAttemptsByIpSince(String ipAddress, LocalDateTime since);
}
