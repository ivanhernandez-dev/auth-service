package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.request.RequestPasswordResetRequest;
import dev.ivanhernandez.authservice.application.port.output.EmailSender;
import dev.ivanhernandez.authservice.application.port.output.PasswordResetTokenRepository;
import dev.ivanhernandez.authservice.application.port.output.TokenGenerator;
import dev.ivanhernandez.authservice.application.port.output.UserRepository;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestPasswordResetUseCaseImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetTokenRepository tokenRepository;
    @Mock
    private TokenGenerator tokenGenerator;
    @Mock
    private EmailSender emailSender;

    private RequestPasswordResetUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RequestPasswordResetUseCaseImpl(userRepository, tokenRepository, tokenGenerator, emailSender);
    }

    @Test
    @DisplayName("requestReset should send reset email when user exists")
    void requestReset_shouldSendResetEmail_whenUserExists() {
        UUID userId = UUID.randomUUID();
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());
        User user = new User(userId, tenant, "john@acme.com", "hash", "John", "Doe",
                true, true, Set.of(Role.USER), LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findByEmailAndTenantSlug("john@acme.com", "acme")).thenReturn(Optional.of(user));
        when(tokenGenerator.generateSecureToken(32)).thenReturn("resetToken");

        RequestPasswordResetRequest request = new RequestPasswordResetRequest("acme", "john@acme.com");
        useCase.requestReset(request);

        verify(tokenRepository).deleteByUserId(userId);
        verify(tokenRepository).save(any());
        verify(emailSender).sendPasswordResetEmail("john@acme.com", "John", "resetToken");
    }

    @Test
    @DisplayName("requestReset should not throw when user not found (security)")
    void requestReset_shouldNotThrow_whenUserNotFound() {
        when(userRepository.findByEmailAndTenantSlug("unknown@acme.com", "acme")).thenReturn(Optional.empty());

        RequestPasswordResetRequest request = new RequestPasswordResetRequest("acme", "unknown@acme.com");
        useCase.requestReset(request);

        verify(tokenRepository, never()).save(any());
        verify(emailSender, never()).sendPasswordResetEmail(any(), any(), any());
    }
}
