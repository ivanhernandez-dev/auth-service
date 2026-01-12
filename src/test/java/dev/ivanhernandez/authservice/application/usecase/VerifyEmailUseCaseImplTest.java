package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.port.output.EmailSender;
import dev.ivanhernandez.authservice.application.port.output.EmailVerificationTokenRepository;
import dev.ivanhernandez.authservice.application.port.output.UserRepository;
import dev.ivanhernandez.authservice.domain.exception.InvalidTokenException;
import dev.ivanhernandez.authservice.domain.exception.TokenExpiredException;
import dev.ivanhernandez.authservice.domain.exception.UserNotFoundException;
import dev.ivanhernandez.authservice.domain.model.EmailVerificationToken;
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
class VerifyEmailUseCaseImplTest {

    @Mock
    private EmailVerificationTokenRepository tokenRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailSender emailSender;

    private VerifyEmailUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new VerifyEmailUseCaseImpl(tokenRepository, userRepository, emailSender);
    }

    @Test
    @DisplayName("verify should verify email and send welcome email")
    void verify_shouldVerifyEmailAndSendWelcomeEmail() {
        UUID userId = UUID.randomUUID();
        EmailVerificationToken token = new EmailVerificationToken(
                UUID.randomUUID(), userId, "token123",
                LocalDateTime.now().plusHours(1), false, LocalDateTime.now()
        );
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());
        User user = new User(userId, tenant, "john@acme.com", "hash", "John", "Doe",
                false, true, Set.of(Role.USER), LocalDateTime.now(), LocalDateTime.now());

        when(tokenRepository.findByToken("token123")).thenReturn(Optional.of(token));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        useCase.verify("token123");

        verify(userRepository).save(any());
        verify(tokenRepository).save(any());
        verify(emailSender).sendWelcomeEmail("john@acme.com", "John");
    }

    @Test
    @DisplayName("verify should throw InvalidTokenException when token not found")
    void verify_shouldThrowInvalidTokenException_whenTokenNotFound() {
        when(tokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> useCase.verify("invalid"));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("verify should throw InvalidTokenException when token already used")
    void verify_shouldThrowInvalidTokenException_whenTokenAlreadyUsed() {
        EmailVerificationToken token = new EmailVerificationToken(
                UUID.randomUUID(), UUID.randomUUID(), "token123",
                LocalDateTime.now().plusHours(1), true, LocalDateTime.now()
        );

        when(tokenRepository.findByToken("token123")).thenReturn(Optional.of(token));

        assertThrows(InvalidTokenException.class, () -> useCase.verify("token123"));
    }

    @Test
    @DisplayName("verify should throw TokenExpiredException when token expired")
    void verify_shouldThrowTokenExpiredException_whenTokenExpired() {
        EmailVerificationToken token = new EmailVerificationToken(
                UUID.randomUUID(), UUID.randomUUID(), "token123",
                LocalDateTime.now().minusHours(1), false, LocalDateTime.now().minusDays(2)
        );

        when(tokenRepository.findByToken("token123")).thenReturn(Optional.of(token));

        assertThrows(TokenExpiredException.class, () -> useCase.verify("token123"));
    }

    @Test
    @DisplayName("verify should throw UserNotFoundException when user not found")
    void verify_shouldThrowUserNotFoundException_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        EmailVerificationToken token = new EmailVerificationToken(
                UUID.randomUUID(), userId, "token123",
                LocalDateTime.now().plusHours(1), false, LocalDateTime.now()
        );

        when(tokenRepository.findByToken("token123")).thenReturn(Optional.of(token));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> useCase.verify("token123"));
    }
}
