package dev.ivanhernandez.authservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {

    @Test
    @DisplayName("create should create token with 30 days expiration")
    void create_shouldCreateTokenWith30DaysExpiration() {
        UUID userId = UUID.randomUUID();

        RefreshToken token = RefreshToken.create(userId, "hashedToken");

        assertNotNull(token);
        assertEquals(userId, token.getUserId());
        assertEquals("hashedToken", token.getTokenHash());
        assertFalse(token.isRevoked());
        assertTrue(token.getExpiresAt().isAfter(LocalDateTime.now().plusDays(29)));
        assertTrue(token.getExpiresAt().isBefore(LocalDateTime.now().plusDays(31)));
    }

    @Test
    @DisplayName("isValid should return true for non-expired and non-revoked token")
    void isValid_shouldReturnTrueForValidToken() {
        UUID userId = UUID.randomUUID();
        RefreshToken token = RefreshToken.create(userId, "hashedToken");

        assertTrue(token.isValid());
    }

    @Test
    @DisplayName("isValid should return false for revoked token")
    void isValid_shouldReturnFalseForRevokedToken() {
        UUID userId = UUID.randomUUID();
        RefreshToken token = RefreshToken.create(userId, "hashedToken");

        token.revoke();

        assertFalse(token.isValid());
        assertTrue(token.isRevoked());
    }

    @Test
    @DisplayName("isExpired should return true for expired token")
    void isExpired_shouldReturnTrueForExpiredToken() {
        UUID userId = UUID.randomUUID();
        RefreshToken token = new RefreshToken(
                UUID.randomUUID(),
                userId,
                "hashedToken",
                LocalDateTime.now().minusDays(1),
                false,
                LocalDateTime.now().minusDays(31)
        );

        assertTrue(token.isExpired());
        assertFalse(token.isValid());
    }
}
