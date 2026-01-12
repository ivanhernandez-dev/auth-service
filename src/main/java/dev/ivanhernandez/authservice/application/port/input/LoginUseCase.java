package dev.ivanhernandez.authservice.application.port.input;

import dev.ivanhernandez.authservice.application.dto.request.LoginRequest;
import dev.ivanhernandez.authservice.application.dto.response.AuthResponse;

public interface LoginUseCase {

    AuthResponse login(LoginRequest request, String ipAddress, String userAgent);
}
