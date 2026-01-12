package dev.ivanhernandez.authservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetTokenTest {

    @Test
    @DisplayName("create should create token with 1 hour expiration")
    void create_shouldCreateTokenWith1HourExpiration() {
        UUID userId = UUID.randomUUID();

        PasswordResetToken token = PasswordResetToken.create(userId, "token123");

        assertNotNull(token);
        assertEquals(userId, token.getUserId());
        assertEquals("token123", token.getToken());
        assertFalse(token.isUsed());
        assertTrue(token.getExpiresAt().isAfter(LocalDateTime.now().plusMinutes(59)));
        assertTrue(token.getExpiresAt().isBefore(LocalDateTime.now().plusMinutes(61)));
    }

    @Test
    @DisplayName("isValid should return true for valid token")
    void isValid_shouldReturnTrue_forValidToken() {
        PasswordResetToken token = PasswordResetToken.create(UUID.randomUUID(), "token");

        assertTrue(token.isValid());
    }

    @Test
    @DisplayName("isValid should return false for used token")
    void isValid_shouldReturnFalse_forUsedToken() {
        PasswordResetToken token = PasswordResetToken.create(UUID.randomUUID(), "token");

        token.markAsUsed();

        assertFalse(token.isValid());
        assertTrue(token.isUsed());
    }

    @Test
    @DisplayName("isExpired should return true for expired token")
    void isExpired_shouldReturnTrue_forExpiredToken() {
        PasswordResetToken token = new PasswordResetToken(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "token",
                LocalDateTime.now().minusMinutes(1),
                false,
                LocalDateTime.now().minusHours(2)
        );

        assertTrue(token.isExpired());
        assertFalse(token.isValid());
    }
}
