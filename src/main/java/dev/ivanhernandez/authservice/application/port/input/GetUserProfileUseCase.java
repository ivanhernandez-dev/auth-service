package dev.ivanhernandez.authservice.application.port.input;

import dev.ivanhernandez.authservice.application.dto.response.UserProfileResponse;

import java.util.UUID;

public interface GetUserProfileUseCase {

    UserProfileResponse getProfile(UUID userId);
}
