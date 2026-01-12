package dev.ivanhernandez.authservice.application.port.input;

import dev.ivanhernandez.authservice.application.dto.request.UpdateUserProfileRequest;
import dev.ivanhernandez.authservice.application.dto.response.UserProfileResponse;

import java.util.UUID;

public interface UpdateUserProfileUseCase {

    UserProfileResponse update(UUID userId, UpdateUserProfileRequest request);
}
