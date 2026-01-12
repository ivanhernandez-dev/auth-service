package dev.ivanhernandez.authservice.application.dto.response;

import dev.ivanhernandez.authservice.domain.model.Role;
import dev.ivanhernandez.authservice.domain.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String tenantSlug,
        String tenantName,
        List<String> roles,
        boolean emailVerified,
        LocalDateTime createdAt
) {
    public static UserProfileResponse fromDomain(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getTenant().getSlug(),
                user.getTenant().getName(),
                user.getRoles().stream().map(Role::name).toList(),
                user.isEmailVerified(),
                user.getCreatedAt()
        );
    }
}
