package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.response.TokenResponse;
import dev.ivanhernandez.authservice.application.port.output.JwtProvider;
import dev.ivanhernandez.authservice.application.port.output.RefreshTokenRepository;
import dev.ivanhernandez.authservice.application.port.output.UserRepository;
import dev.ivanhernandez.authservice.domain.exception.InvalidTokenException;
import dev.ivanhernandez.authservice.domain.exception.TokenExpiredException;
import dev.ivanhernandez.authservice.domain.exception.TokenRevokedException;
import dev.ivanhernandez.authservice.domain.model.RefreshToken;
import dev.ivanhernandez.authservice.domain.model.Role;
import dev.ivanhernandez.authservice.domain.model.Tenant;
import dev.ivanhernandez.authservice.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtProvider jwtProvider;

    private RefreshTokenUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RefreshTokenUseCaseImpl(refreshTokenRepository, userRepository, jwtProvider);
    }

    @Test
    @DisplayName("refresh should return new access token for valid refresh token")
    void refresh_shouldReturnNewAccessToken_forValidRefreshToken() {
        UUID userId = UUID.randomUUID();
        RefreshToken storedToken = new RefreshToken(
                UUID.randomUUID(), userId, "hashedToken",
                LocalDateTime.now().plusDays(30), false, LocalDateTime.now()
        );
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());
        User user = new User(userId, tenant, "john@acme.com", "hash", "John", "Doe",
                true, true, Set.of(Role.USER), LocalDateTime.now(), LocalDateTime.now());

        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(storedToken));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtProvider.generateAccessToken(user)).thenReturn("newAccessToken");
        when(jwtProvider.getAccessTokenExpirationMs()).thenReturn(900000L);

        TokenResponse response = useCase.refresh("refreshToken");

        assertNotNull(response);
        assertEquals("newAccessToken", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(900, response.expiresIn());
    }

    @Test
    @DisplayName("refresh should throw InvalidTokenException when token not found")
    void refresh_shouldThrowInvalidTokenException_whenTokenNotFound() {
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> useCase.refresh("invalidToken"));
    }

    @Test
    @DisplayName("refresh should throw TokenRevokedException when token is revoked")
    void refresh_shouldThrowTokenRevokedException_whenTokenIsRevoked() {
        RefreshToken storedToken = new RefreshToken(
                UUID.randomUUID(), UUID.randomUUID(), "hashedToken",
                LocalDateTime.now().plusDays(30), true, LocalDateTime.now()
        );

        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(storedToken));

        assertThrows(TokenRevokedException.class, () -> useCase.refresh("refreshToken"));
    }

    @Test
    @DisplayName("refresh should throw TokenExpiredException when token is expired")
    void refresh_shouldThrowTokenExpiredException_whenTokenIsExpired() {
        RefreshToken storedToken = new RefreshToken(
                UUID.randomUUID(), UUID.randomUUID(), "hashedToken",
                LocalDateTime.now().minusDays(1), false, LocalDateTime.now().minusDays(31)
        );

        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(storedToken));

        assertThrows(TokenExpiredException.class, () -> useCase.refresh("refreshToken"));
    }
}
