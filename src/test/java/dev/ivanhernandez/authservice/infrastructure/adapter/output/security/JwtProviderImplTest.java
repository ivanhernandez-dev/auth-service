package dev.ivanhernandez.authservice.infrastructure.adapter.output.security;

import dev.ivanhernandez.authservice.domain.model.Role;
import dev.ivanhernandez.authservice.domain.model.Tenant;
import dev.ivanhernandez.authservice.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderImplTest {

    private JwtProviderImpl jwtProvider;

    private static final String SECRET = "test-secret-key-must-be-at-least-32-characters-long-for-hmac";
    private static final long EXPIRATION_MS = 900000L;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProviderImpl(SECRET, EXPIRATION_MS);
    }

    private User createTestUser() {
        UUID userId = UUID.randomUUID();
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());
        return new User(
                userId,
                tenant,
                "test@acme.com",
                "hashedPassword",
                "Test",
                "User",
                true,
                true,
                Set.of(Role.USER, Role.ADMIN),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("generateAccessToken should create valid JWT")
    void generateAccessToken_shouldCreateValidJwt() {
        User user = createTestUser();

        String token = jwtProvider.generateAccessToken(user);

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    @DisplayName("validateToken should return true for valid token")
    void validateToken_shouldReturnTrue_forValidToken() {
        User user = createTestUser();
        String token = jwtProvider.generateAccessToken(user);

        boolean isValid = jwtProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("validateToken should return false for invalid token")
    void validateToken_shouldReturnFalse_forInvalidToken() {
        boolean isValid = jwtProvider.validateToken("invalid.token.here");

        assertFalse(isValid);
    }

    @Test
    @DisplayName("validateToken should return false for tampered token")
    void validateToken_shouldReturnFalse_forTamperedToken() {
        User user = createTestUser();
        String token = jwtProvider.generateAccessToken(user);
        String tamperedToken = token.substring(0, token.length() - 5) + "XXXXX";

        boolean isValid = jwtProvider.validateToken(tamperedToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("getUserIdFromToken should return correct userId")
    void getUserIdFromToken_shouldReturnCorrectUserId() {
        User user = createTestUser();
        String token = jwtProvider.generateAccessToken(user);

        String userId = jwtProvider.getUserIdFromToken(token);

        assertEquals(user.getId().toString(), userId);
    }

    @Test
    @DisplayName("getTenantIdFromToken should return correct tenantId")
    void getTenantIdFromToken_shouldReturnCorrectTenantId() {
        User user = createTestUser();
        String token = jwtProvider.generateAccessToken(user);

        String tenantId = jwtProvider.getTenantIdFromToken(token);

        assertEquals(user.getTenant().getId().toString(), tenantId);
    }

    @Test
    @DisplayName("getAccessTokenExpirationMs should return configured value")
    void getAccessTokenExpirationMs_shouldReturnConfiguredValue() {
        assertEquals(EXPIRATION_MS, jwtProvider.getAccessTokenExpirationMs());
    }

    @Test
    @DisplayName("getRemainingExpirationSeconds should return positive value for valid token")
    void getRemainingExpirationSeconds_shouldReturnPositiveValue_forValidToken() {
        User user = createTestUser();
        String token = jwtProvider.generateAccessToken(user);

        long remainingSeconds = jwtProvider.getRemainingExpirationSeconds(token);

        assertTrue(remainingSeconds > 0);
        assertTrue(remainingSeconds <= EXPIRATION_MS / 1000);
    }

    @Test
    @DisplayName("getRemainingExpirationSeconds should return zero for invalid token")
    void getRemainingExpirationSeconds_shouldReturnZero_forInvalidToken() {
        long remainingSeconds = jwtProvider.getRemainingExpirationSeconds("invalid.token.here");

        assertEquals(0, remainingSeconds);
    }
}
