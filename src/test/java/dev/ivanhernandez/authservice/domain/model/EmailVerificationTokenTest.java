package dev.ivanhernandez.authservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmailVerificationTokenTest {

    @Test
    @DisplayName("create should create token with 24 hours expiration")
    void create_shouldCreateTokenWith24HoursExpiration() {
        UUID userId = UUID.randomUUID();

        EmailVerificationToken token = EmailVerificationToken.create(userId, "token123");

        assertNotNull(token);
        assertEquals(userId, token.getUserId());
        assertEquals("token123", token.getToken());
        assertFalse(token.isUsed());
        assertTrue(token.getExpiresAt().isAfter(LocalDateTime.now().plusHours(23)));
        assertTrue(token.getExpiresAt().isBefore(LocalDateTime.now().plusHours(25)));
    }

    @Test
    @DisplayName("isValid should return true for valid token")
    void isValid_shouldReturnTrue_forValidToken() {
        EmailVerificationToken token = EmailVerificationToken.create(UUID.randomUUID(), "token");

        assertTrue(token.isValid());
    }

    @Test
    @DisplayName("isValid should return false for used token")
    void isValid_shouldReturnFalse_forUsedToken() {
        EmailVerificationToken token = EmailVerificationToken.create(UUID.randomUUID(), "token");

        token.markAsUsed();

        assertFalse(token.isValid());
        assertTrue(token.isUsed());
    }

    @Test
    @DisplayName("isExpired should return true for expired token")
    void isExpired_shouldReturnTrue_forExpiredToken() {
        EmailVerificationToken token = new EmailVerificationToken(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "token",
                LocalDateTime.now().minusHours(1),
                false,
                LocalDateTime.now().minusDays(2)
        );

        assertTrue(token.isExpired());
        assertFalse(token.isValid());
    }
}
