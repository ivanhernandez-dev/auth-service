package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.response.UserProfileResponse;
import dev.ivanhernandez.authservice.application.port.output.UserRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserProfileUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    private GetUserProfileUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetUserProfileUseCaseImpl(userRepository);
    }

    @Test
    @DisplayName("getProfile should return user profile")
    void getProfile_shouldReturnUserProfile() {
        UUID userId = UUID.randomUUID();
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());
        User user = new User(userId, tenant, "john@acme.com", "hash", "John", "Doe",
                true, true, Set.of(Role.USER), LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserProfileResponse response = useCase.getProfile(userId);

        assertNotNull(response);
        assertEquals("john@acme.com", response.email());
        assertEquals("John", response.firstName());
        assertEquals("Doe", response.lastName());
        assertEquals("acme", response.tenantSlug());
    }

    @Test
    @DisplayName("getProfile should throw UserNotFoundException when user not found")
    void getProfile_shouldThrowUserNotFoundException_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> useCase.getProfile(userId));
    }
}
