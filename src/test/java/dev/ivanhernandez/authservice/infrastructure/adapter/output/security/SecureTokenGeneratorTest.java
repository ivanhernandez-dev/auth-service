package dev.ivanhernandez.authservice.infrastructure.adapter.output.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecureTokenGeneratorTest {

    private SecureTokenGenerator tokenGenerator;

    @BeforeEach
    void setUp() {
        tokenGenerator = new SecureTokenGenerator();
    }

    @Test
    @DisplayName("generateToken should create unique tokens")
    void generateToken_shouldCreateUniqueTokens() {
        String token1 = tokenGenerator.generateToken();
        String token2 = tokenGenerator.generateToken();

        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("generateToken should create 32 character token")
    void generateToken_shouldCreate32CharacterToken() {
        String token = tokenGenerator.generateToken();

        assertEquals(32, token.length());
    }

    @Test
    @DisplayName("generateSecureToken should create token of specified length")
    void generateSecureToken_shouldCreateTokenOfSpecifiedLength() {
        String token16 = tokenGenerator.generateSecureToken(16);
        String token32 = tokenGenerator.generateSecureToken(32);

        assertNotNull(token16);
        assertNotNull(token32);
        assertTrue(token32.length() > token16.length());
    }

    @Test
    @DisplayName("generateSecureToken should create unique tokens")
    void generateSecureToken_shouldCreateUniqueTokens() {
        String token1 = tokenGenerator.generateSecureToken(32);
        String token2 = tokenGenerator.generateSecureToken(32);

        assertNotEquals(token1, token2);
    }
}
