package dev.ivanhernandez.authservice.application.port.input;

import dev.ivanhernandez.authservice.application.dto.response.TokenResponse;

public interface RefreshTokenUseCase {

    TokenResponse refresh(String refreshToken);
}
