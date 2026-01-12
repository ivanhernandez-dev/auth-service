package dev.ivanhernandez.authservice.application.dto.response;

import java.util.List;
import java.util.UUID;

public record IntrospectResponse(
        boolean active,
        UUID userId,
        UUID tenantId,
        String tenantSlug,
        String email,
        List<String> roles
) {
    public static IntrospectResponse inactive() {
        return new IntrospectResponse(false, null, null, null, null, null);
    }

    public static IntrospectResponse active(UUID userId, UUID tenantId, String tenantSlug,
                                            String email, List<String> roles) {
        return new IntrospectResponse(true, userId, tenantId, tenantSlug, email, roles);
    }
}
