package dev.ivanhernandez.authservice.application.port.input;

import dev.ivanhernandez.authservice.application.dto.request.ResetPasswordRequest;

public interface ResetPasswordUseCase {

    void reset(ResetPasswordRequest request);
}
