package dev.ivanhernandez.authservice.infrastructure.adapter.output.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BCryptPasswordEncoderAdapterTest {

    private BCryptPasswordEncoderAdapter passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoderAdapter();
    }

    @Test
    @DisplayName("encode should hash password")
    void encode_shouldHashPassword() {
        String rawPassword = "SecurePass1!";

        String encoded = passwordEncoder.encode(rawPassword);

        assertNotNull(encoded);
        assertNotEquals(rawPassword, encoded);
        assertTrue(encoded.startsWith("$2a$"));
    }

    @Test
    @DisplayName("encode should produce different hashes for same password")
    void encode_shouldProduceDifferentHashes_forSamePassword() {
        String rawPassword = "SecurePass1!";

        String encoded1 = passwordEncoder.encode(rawPassword);
        String encoded2 = passwordEncoder.encode(rawPassword);

        assertNotEquals(encoded1, encoded2);
    }

    @Test
    @DisplayName("matches should return true for matching password")
    void matches_shouldReturnTrue_forMatchingPassword() {
        String rawPassword = "SecurePass1!";
        String encoded = passwordEncoder.encode(rawPassword);

        boolean matches = passwordEncoder.matches(rawPassword, encoded);

        assertTrue(matches);
    }

    @Test
    @DisplayName("matches should return false for non-matching password")
    void matches_shouldReturnFalse_forNonMatchingPassword() {
        String rawPassword = "SecurePass1!";
        String wrongPassword = "WrongPass1!";
        String encoded = passwordEncoder.encode(rawPassword);

        boolean matches = passwordEncoder.matches(wrongPassword, encoded);

        assertFalse(matches);
    }
}
