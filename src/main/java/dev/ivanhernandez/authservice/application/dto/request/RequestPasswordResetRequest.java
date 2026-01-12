package dev.ivanhernandez.authservice.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequestPasswordResetRequest(
        @NotBlank(message = "Tenant slug is required")
        String tenantSlug,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {
}
