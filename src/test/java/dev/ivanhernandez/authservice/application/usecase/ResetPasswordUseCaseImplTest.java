package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.request.ResetPasswordRequest;
import dev.ivanhernandez.authservice.application.port.output.PasswordEncoder;
import dev.ivanhernandez.authservice.application.port.output.PasswordResetTokenRepository;
import dev.ivanhernandez.authservice.application.port.output.RefreshTokenRepository;
import dev.ivanhernandez.authservice.application.port.output.UserRepository;
import dev.ivanhernandez.authservice.domain.exception.InvalidTokenException;
import dev.ivanhernandez.authservice.domain.exception.TokenExpiredException;
import dev.ivanhernandez.authservice.domain.model.PasswordResetToken;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordUseCaseImplTest {

    @Mock
    private PasswordResetTokenRepository tokenRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private ResetPasswordUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ResetPasswordUseCaseImpl(tokenRepository, userRepository, refreshTokenRepository, passwordEncoder);
    }

    @Test
    @DisplayName("reset should update password and revoke all tokens")
    void reset_shouldUpdatePasswordAndRevokeAllTokens() {
        UUID userId = UUID.randomUUID();
        PasswordResetToken token = new PasswordResetToken(
                UUID.randomUUID(), userId, "token123",
                LocalDateTime.now().plusHours(1), false, LocalDateTime.now()
        );
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());
        User user = new User(userId, tenant, "john@acme.com", "oldHash", "John", "Doe",
                true, true, Set.of(Role.USER), LocalDateTime.now(), LocalDateTime.now());

        when(tokenRepository.findByToken("token123")).thenReturn(Optional.of(token));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("NewSecurePass1!")).thenReturn("newHash");
        when(userRepository.save(any())).thenReturn(user);

        ResetPasswordRequest request = new ResetPasswordRequest("token123", "NewSecurePass1!");
        useCase.reset(request);

        verify(userRepository).save(any());
        verify(tokenRepository).save(any());
        verify(refreshTokenRepository).revokeAllByUserId(userId);
    }

    @Test
    @DisplayName("reset should throw InvalidTokenException when token not found")
    void reset_shouldThrowInvalidTokenException_whenTokenNotFound() {
        when(tokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        ResetPasswordRequest request = new ResetPasswordRequest("invalid", "NewSecurePass1!");
        assertThrows(InvalidTokenException.class, () -> useCase.reset(request));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("reset should throw TokenExpiredException when token expired")
    void reset_shouldThrowTokenExpiredException_whenTokenExpired() {
        PasswordResetToken token = new PasswordResetToken(
                UUID.randomUUID(), UUID.randomUUID(), "token123",
                LocalDateTime.now().minusHours(1), false, LocalDateTime.now().minusHours(2)
        );

        when(tokenRepository.findByToken("token123")).thenReturn(Optional.of(token));

        ResetPasswordRequest request = new ResetPasswordRequest("token123", "NewSecurePass1!");
        assertThrows(TokenExpiredException.class, () -> useCase.reset(request));
    }
}
