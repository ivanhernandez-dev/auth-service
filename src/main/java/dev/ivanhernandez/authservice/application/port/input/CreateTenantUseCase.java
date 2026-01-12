package dev.ivanhernandez.authservice.application.port.input;

import dev.ivanhernandez.authservice.application.dto.request.CreateTenantRequest;
import dev.ivanhernandez.authservice.application.dto.response.TenantResponse;

public interface CreateTenantUseCase {

    TenantResponse create(CreateTenantRequest request);
}
