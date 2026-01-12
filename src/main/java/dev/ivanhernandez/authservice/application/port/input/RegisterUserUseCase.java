package dev.ivanhernandez.authservice.application.port.input;

import dev.ivanhernandez.authservice.application.dto.request.RegisterRequest;
import dev.ivanhernandez.authservice.application.dto.response.UserProfileResponse;

public interface RegisterUserUseCase {

    UserProfileResponse register(RegisterRequest request);
}
