package dev.ivanhernandez.authservice.application.port.input;

import java.util.UUID;

public interface LogoutUseCase {

    void logout(UUID userId, String accessToken, String refreshToken);

    void logoutAllDevices(UUID userId, String accessToken);
}
