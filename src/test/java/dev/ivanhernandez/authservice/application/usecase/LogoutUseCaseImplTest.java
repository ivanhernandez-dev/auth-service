package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.port.output.JwtProvider;
import dev.ivanhernandez.authservice.application.port.output.RefreshTokenRepository;
import dev.ivanhernandez.authservice.application.port.output.TokenBlacklist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenBlacklist tokenBlacklist;

    @Mock
    private JwtProvider jwtProvider;

    private LogoutUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new LogoutUseCaseImpl(refreshTokenRepository, tokenBlacklist, jwtProvider);
    }

    @Test
    @DisplayName("logout should revoke only the specific refresh token and blacklist access token")
    void logout_shouldRevokeOnlySpecificRefreshToken() {
        UUID userId = UUID.randomUUID();
        String accessToken = "valid.jwt.token";
        String refreshToken = "refresh-token-value";

        when(jwtProvider.getRemainingExpirationSeconds(accessToken)).thenReturn(300L);

        useCase.logout(userId, accessToken, refreshToken);

        verify(tokenBlacklist).blacklist(accessToken, 300L);
        verify(refreshTokenRepository).revokeByTokenHash(anyString());
    }

    @Test
    @DisplayName("logoutAllDevices should revoke all user tokens and blacklist access token")
    void logoutAllDevices_shouldRevokeAllUserTokens() {
        UUID userId = UUID.randomUUID();
        String accessToken = "valid.jwt.token";

        when(jwtProvider.getRemainingExpirationSeconds(accessToken)).thenReturn(600L);

        useCase.logoutAllDevices(userId, accessToken);

        verify(tokenBlacklist).blacklist(accessToken, 600L);
        verify(refreshTokenRepository).revokeAllByUserId(userId);
    }

    @Test
    @DisplayName("logout should handle null access token gracefully")
    void logout_shouldHandleNullAccessToken() {
        UUID userId = UUID.randomUUID();
        String refreshToken = "refresh-token-value";

        useCase.logout(userId, null, refreshToken);

        verify(refreshTokenRepository).revokeByTokenHash(anyString());
    }
}
