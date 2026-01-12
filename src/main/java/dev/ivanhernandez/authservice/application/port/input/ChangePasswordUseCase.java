package dev.ivanhernandez.authservice.application.port.input;

import dev.ivanhernandez.authservice.application.dto.request.ChangePasswordRequest;

import java.util.UUID;

public interface ChangePasswordUseCase {

    void changePassword(UUID userId, ChangePasswordRequest request);
}
