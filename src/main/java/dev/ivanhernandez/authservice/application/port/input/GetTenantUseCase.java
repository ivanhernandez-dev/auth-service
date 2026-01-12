package dev.ivanhernandez.authservice.application.port.input;

import dev.ivanhernandez.authservice.application.dto.response.TenantResponse;

public interface GetTenantUseCase {

    TenantResponse getBySlug(String slug);
}
