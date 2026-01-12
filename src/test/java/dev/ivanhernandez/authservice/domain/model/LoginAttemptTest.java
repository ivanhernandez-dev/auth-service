package dev.ivanhernandez.authservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptTest {

    @Test
    @DisplayName("success should create successful login attempt")
    void success_shouldCreateSuccessfulLoginAttempt() {
        UUID userId = UUID.randomUUID();

        LoginAttempt attempt = LoginAttempt.success(
                userId,
                "john@acme.com",
                "acme",
                "127.0.0.1",
                "Mozilla/5.0"
        );

        assertNotNull(attempt);
        assertEquals(userId, attempt.getUserId());
        assertEquals("john@acme.com", attempt.getEmail());
        assertEquals("acme", attempt.getTenantSlug());
        assertEquals("127.0.0.1", attempt.getIpAddress());
        assertTrue(attempt.isSuccess());
        assertNotNull(attempt.getAttemptedAt());
    }

    @Test
    @DisplayName("failure should create failed login attempt without userId")
    void failure_shouldCreateFailedLoginAttemptWithoutUserId() {
        LoginAttempt attempt = LoginAttempt.failure(
                "john@acme.com",
                "acme",
                "127.0.0.1",
                "Mozilla/5.0"
        );

        assertNotNull(attempt);
        assertNull(attempt.getUserId());
        assertEquals("john@acme.com", attempt.getEmail());
        assertEquals("acme", attempt.getTenantSlug());
        assertFalse(attempt.isSuccess());
    }
}
