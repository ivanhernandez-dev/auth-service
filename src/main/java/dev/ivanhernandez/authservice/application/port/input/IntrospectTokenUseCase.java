package dev.ivanhernandez.authservice.application.port.input;

import dev.ivanhernandez.authservice.application.dto.response.IntrospectResponse;

public interface IntrospectTokenUseCase {

    IntrospectResponse introspect(String token);
}
