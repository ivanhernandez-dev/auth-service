package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.request.UpdateUserProfileRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserProfileUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    private UpdateUserProfileUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateUserProfileUseCaseImpl(userRepository);
    }

    @Test
    @DisplayName("update should update user profile")
    void update_shouldUpdateUserProfile() {
        UUID userId = UUID.randomUUID();
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());
        User user = new User(userId, tenant, "john@acme.com", "hash", "John", "Doe",
                true, true, Set.of(Role.USER), LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdateUserProfileRequest request = new UpdateUserProfileRequest("Jane", "Smith");
        UserProfileResponse response = useCase.update(userId, request);

        assertNotNull(response);
        assertEquals("Jane", response.firstName());
        assertEquals("Smith", response.lastName());
    }

    @Test
    @DisplayName("update should throw UserNotFoundException when user not found")
    void update_shouldThrowUserNotFoundException_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UpdateUserProfileRequest request = new UpdateUserProfileRequest("Jane", "Smith");
        assertThrows(UserNotFoundException.class, () -> useCase.update(userId, request));
    }
}
