package dev.ivanhernandez.authservice.application.dto.response;

import dev.ivanhernandez.authservice.domain.model.Tenant;

import java.time.LocalDateTime;
import java.util.UUID;

public record TenantResponse(
        UUID id,
        String name,
        String slug,
        boolean enabled,
        LocalDateTime createdAt
) {
    public static TenantResponse fromDomain(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getName(),
                tenant.getSlug(),
                tenant.isEnabled(),
                tenant.getCreatedAt()
        );
    }
}
