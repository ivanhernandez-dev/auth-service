package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.request.RegisterRequest;
import dev.ivanhernandez.authservice.application.dto.response.UserProfileResponse;
import dev.ivanhernandez.authservice.application.port.output.*;
import dev.ivanhernandez.authservice.domain.exception.TenantNotFoundException;
import dev.ivanhernandez.authservice.domain.exception.UserAlreadyExistsException;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private EmailVerificationTokenRepository verificationTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenGenerator tokenGenerator;
    @Mock
    private EmailSender emailSender;

    private RegisterUserUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterUserUseCaseImpl(
                userRepository,
                tenantRepository,
                verificationTokenRepository,
                passwordEncoder,
                tokenGenerator,
                emailSender
        );
    }

    @Test
    @DisplayName("register should create user and send verification email")
    void register_shouldCreateUserAndSendVerificationEmail() {
        RegisterRequest request = new RegisterRequest(
                "acme",
                "john@acme.com",
                "SecurePass1!",
                "John",
                "Doe"
        );

        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());

        when(tenantRepository.findBySlug("acme")).thenReturn(Optional.of(tenant));
        when(userRepository.existsByEmailAndTenantId(any(), any())).thenReturn(false);
        when(passwordEncoder.encode("SecurePass1!")).thenReturn("hashedPassword");
        when(tokenGenerator.generateSecureToken(32)).thenReturn("verificationToken");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return new User(
                    UUID.randomUUID(),
                    user.getTenant(),
                    user.getEmail(),
                    user.getPasswordHash(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.isEmailVerified(),
                    user.isEnabled(),
                    user.getRoles(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );
        });

        UserProfileResponse response = useCase.register(request);

        assertNotNull(response);
        assertEquals("john@acme.com", response.email());
        assertEquals("John", response.firstName());
        assertEquals("Doe", response.lastName());
        assertFalse(response.emailVerified());

        verify(emailSender).sendVerificationEmail(eq("john@acme.com"), eq("John"), eq("verificationToken"));
        verify(verificationTokenRepository).save(any());
    }

    @Test
    @DisplayName("register should throw TenantNotFoundException when tenant not found")
    void register_shouldThrowTenantNotFoundException_whenTenantNotFound() {
        RegisterRequest request = new RegisterRequest(
                "unknown",
                "john@acme.com",
                "SecurePass1!",
                "John",
                "Doe"
        );

        when(tenantRepository.findBySlug("unknown")).thenReturn(Optional.empty());

        assertThrows(TenantNotFoundException.class, () -> useCase.register(request));

        verify(userRepository, never()).save(any());
        verify(emailSender, never()).sendVerificationEmail(any(), any(), any());
    }

    @Test
    @DisplayName("register should throw UserAlreadyExistsException when email exists")
    void register_shouldThrowUserAlreadyExistsException_whenEmailExists() {
        RegisterRequest request = new RegisterRequest(
                "acme",
                "john@acme.com",
                "SecurePass1!",
                "John",
                "Doe"
        );

        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());

        when(tenantRepository.findBySlug("acme")).thenReturn(Optional.of(tenant));
        when(userRepository.existsByEmailAndTenantId("john@acme.com", tenant.getId())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> useCase.register(request));

        verify(userRepository, never()).save(any());
    }
}
