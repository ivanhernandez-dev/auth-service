package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.request.ChangePasswordRequest;
import dev.ivanhernandez.authservice.application.port.output.PasswordEncoder;
import dev.ivanhernandez.authservice.application.port.output.RefreshTokenRepository;
import dev.ivanhernandez.authservice.application.port.output.UserRepository;
import dev.ivanhernandez.authservice.domain.exception.InvalidCredentialsException;
import dev.ivanhernandez.authservice.domain.exception.UserNotFoundException;
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
class ChangePasswordUseCaseImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private ChangePasswordUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ChangePasswordUseCaseImpl(userRepository, refreshTokenRepository, passwordEncoder);
    }

    @Test
    @DisplayName("changePassword should update password and revoke tokens")
    void changePassword_shouldUpdatePasswordAndRevokeTokens() {
        UUID userId = UUID.randomUUID();
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());
        User user = new User(userId, tenant, "john@acme.com", "oldHash", "John", "Doe",
                true, true, Set.of(Role.USER), LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("OldPass1!", "oldHash")).thenReturn(true);
        when(passwordEncoder.encode("NewPass1!")).thenReturn("newHash");
        when(userRepository.save(any())).thenReturn(user);

        ChangePasswordRequest request = new ChangePasswordRequest("OldPass1!", "NewPass1!");
        useCase.changePassword(userId, request);

        verify(userRepository).save(any());
        verify(refreshTokenRepository).revokeAllByUserId(userId);
    }

    @Test
    @DisplayName("changePassword should throw UserNotFoundException when user not found")
    void changePassword_shouldThrowUserNotFoundException_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ChangePasswordRequest request = new ChangePasswordRequest("OldPass1!", "NewPass1!");
        assertThrows(UserNotFoundException.class, () -> useCase.changePassword(userId, request));
    }

    @Test
    @DisplayName("changePassword should throw InvalidCredentialsException when current password wrong")
    void changePassword_shouldThrowInvalidCredentialsException_whenCurrentPasswordWrong() {
        UUID userId = UUID.randomUUID();
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());
        User user = new User(userId, tenant, "john@acme.com", "oldHash", "John", "Doe",
                true, true, Set.of(Role.USER), LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPass1!", "oldHash")).thenReturn(false);

        ChangePasswordRequest request = new ChangePasswordRequest("WrongPass1!", "NewPass1!");
        assertThrows(InvalidCredentialsException.class, () -> useCase.changePassword(userId, request));

        verify(userRepository, never()).save(any());
    }
}
