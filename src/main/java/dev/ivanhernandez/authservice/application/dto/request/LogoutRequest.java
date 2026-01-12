package dev.ivanhernandez.authservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {
}
