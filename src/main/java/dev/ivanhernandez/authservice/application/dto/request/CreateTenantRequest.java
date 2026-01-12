package dev.ivanhernandez.authservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateTenantRequest(
        @NotBlank(message = "Tenant name is required")
        @Size(max = 100, message = "Tenant name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Tenant slug is required")
        @Size(min = 3, max = 50, message = "Tenant slug must be between 3 and 50 characters")
        @Pattern(regexp = "^[a-z0-9-]+$", message = "Tenant slug must contain only lowercase letters, numbers, and hyphens")
        String slug
) {
}
