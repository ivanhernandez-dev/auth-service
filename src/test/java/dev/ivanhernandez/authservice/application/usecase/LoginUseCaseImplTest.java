package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.request.LoginRequest;
import dev.ivanhernandez.authservice.application.dto.response.AuthResponse;
import dev.ivanhernandez.authservice.application.port.output.*;
import dev.ivanhernandez.authservice.domain.exception.InvalidCredentialsException;
import dev.ivanhernandez.authservice.domain.exception.UserNotVerifiedException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private LoginAttemptRepository loginAttemptRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private TokenGenerator tokenGenerator;

    private LoginUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new LoginUseCaseImpl(
                userRepository,
                refreshTokenRepository,
                loginAttemptRepository,
                passwordEncoder,
                jwtProvider,
                tokenGenerator
        );
    }

    private User createVerifiedUser(Tenant tenant) {
        return new User(
                UUID.randomUUID(),
                tenant,
                "john@acme.com",
                "hashedPassword",
                "John",
                "Doe",
                true,
                true,
                Set.of(Role.USER),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private User createUnverifiedUser(Tenant tenant) {
        return new User(
                UUID.randomUUID(),
                tenant,
                "john@acme.com",
                "hashedPassword",
                "John",
                "Doe",
                false,
                true,
                Set.of(Role.USER),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("login should return tokens for valid credentials")
    void login_shouldReturnTokens_forValidCredentials() {
        LoginRequest request = new LoginRequest("acme", "john@acme.com", "password");
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());
        User user = createVerifiedUser(tenant);

        when(userRepository.findByEmailAndTenantSlug("john@acme.com", "acme"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashedPassword")).thenReturn(true);
        when(jwtProvider.generateAccessToken(user)).thenReturn("accessToken");
        when(jwtProvider.getAccessTokenExpirationMs()).thenReturn(900000L);
        when(tokenGenerator.generateSecureToken(32)).thenReturn("refreshToken");

        AuthResponse response = useCase.login(request, "127.0.0.1", "Mozilla");

        assertNotNull(response);
        assertEquals("accessToken", response.accessToken());
        assertEquals("refreshToken", response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(900, response.expiresIn());

        verify(refreshTokenRepository).save(any());
        verify(loginAttemptRepository).save(any());
    }

    @Test
    @DisplayName("login should throw InvalidCredentialsException for wrong password")
    void login_shouldThrowInvalidCredentialsException_forWrongPassword() {
        LoginRequest request = new LoginRequest("acme", "john@acme.com", "wrongpassword");
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());
        User user = createVerifiedUser(tenant);

        when(userRepository.findByEmailAndTenantSlug("john@acme.com", "acme"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> useCase.login(request, "127.0.0.1", "Mozilla"));

        verify(loginAttemptRepository).save(any());
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("login should throw InvalidCredentialsException for non-existent user")
    void login_shouldThrowInvalidCredentialsException_forNonExistentUser() {
        LoginRequest request = new LoginRequest("acme", "unknown@acme.com", "password");

        when(userRepository.findByEmailAndTenantSlug("unknown@acme.com", "acme"))
                .thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> useCase.login(request, "127.0.0.1", "Mozilla"));

        verify(loginAttemptRepository).save(any());
    }

    @Test
    @DisplayName("login should throw UserNotVerifiedException for unverified user")
    void login_shouldThrowUserNotVerifiedException_forUnverifiedUser() {
        LoginRequest request = new LoginRequest("acme", "john@acme.com", "password");
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());
        User user = createUnverifiedUser(tenant);

        when(userRepository.findByEmailAndTenantSlug("john@acme.com", "acme"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashedPassword")).thenReturn(true);

        assertThrows(UserNotVerifiedException.class,
                () -> useCase.login(request, "127.0.0.1", "Mozilla"));
    }
}
