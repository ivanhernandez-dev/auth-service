package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.response.IntrospectResponse;
import dev.ivanhernandez.authservice.application.port.input.IntrospectTokenUseCase;
import dev.ivanhernandez.authservice.application.port.output.JwtProvider;
import dev.ivanhernandez.authservice.application.port.output.TokenBlacklist;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IntrospectTokenUseCaseImpl implements IntrospectTokenUseCase {

    private final JwtProvider jwtProvider;
    private final TokenBlacklist tokenBlacklist;

    public IntrospectTokenUseCaseImpl(JwtProvider jwtProvider, TokenBlacklist tokenBlacklist) {
        this.jwtProvider = jwtProvider;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Override
    public IntrospectResponse introspect(String token) {
        if (!jwtProvider.validateToken(token)) {
            return IntrospectResponse.inactive();
        }

        if (tokenBlacklist.isBlacklisted(token)) {
            return IntrospectResponse.inactive();
        }

        try {
            UUID userId = UUID.fromString(jwtProvider.getUserIdFromToken(token));
            UUID tenantId = UUID.fromString(jwtProvider.getTenantIdFromToken(token));
            String tenantSlug = jwtProvider.getTenantSlugFromToken(token);
            String email = jwtProvider.getEmailFromToken(token);
            var roles = jwtProvider.getRolesFromToken(token);

            return IntrospectResponse.active(userId, tenantId, tenantSlug, email, roles);
        } catch (Exception e) {
            return IntrospectResponse.inactive();
        }
    }
}
