package dev.ivanhernandez.authservice.infrastructure.adapter.output.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTokenBlacklistTest {

    private InMemoryTokenBlacklist tokenBlacklist;

    @BeforeEach
    void setUp() {
        tokenBlacklist = new InMemoryTokenBlacklist();
    }

    @Test
    @DisplayName("blacklist should add token to blacklist")
    void blacklist_shouldAddTokenToBlacklist() {
        String tokenId = "test-token-id";

        tokenBlacklist.blacklist(tokenId, 3600);

        assertTrue(tokenBlacklist.isBlacklisted(tokenId));
    }

    @Test
    @DisplayName("isBlacklisted should return false for non-blacklisted token")
    void isBlacklisted_shouldReturnFalse_forNonBlacklistedToken() {
        assertFalse(tokenBlacklist.isBlacklisted("unknown-token"));
    }

    @Test
    @DisplayName("isBlacklisted should return false for expired token")
    void isBlacklisted_shouldReturnFalse_forExpiredToken() {
        String tokenId = "expired-token";

        tokenBlacklist.blacklist(tokenId, 0);

        assertFalse(tokenBlacklist.isBlacklisted(tokenId));
    }

    @Test
    @DisplayName("different tokens should be independent")
    void differentTokens_shouldBeIndependent() {
        String token1 = "token-1";
        String token2 = "token-2";

        tokenBlacklist.blacklist(token1, 3600);

        assertTrue(tokenBlacklist.isBlacklisted(token1));
        assertFalse(tokenBlacklist.isBlacklisted(token2));
    }
}
