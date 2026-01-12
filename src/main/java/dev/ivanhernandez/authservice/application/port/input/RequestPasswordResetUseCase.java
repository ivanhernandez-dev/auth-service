package dev.ivanhernandez.authservice.application.port.input;

import dev.ivanhernandez.authservice.application.dto.request.RequestPasswordResetRequest;

public interface RequestPasswordResetUseCase {

    void requestReset(RequestPasswordResetRequest request);
}
