package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.response.IntrospectResponse;
import dev.ivanhernandez.authservice.application.port.output.JwtProvider;
import dev.ivanhernandez.authservice.application.port.output.TokenBlacklist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("IntrospectTokenUseCaseImpl")
class IntrospectTokenUseCaseImplTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private TokenBlacklist tokenBlacklist;

    private IntrospectTokenUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new IntrospectTokenUseCaseImpl(jwtProvider, tokenBlacklist);
    }

    @Test
    @DisplayName("introspect should return active response for valid token")
    void introspect_shouldReturnActiveResponse_forValidToken() {
        String token = "valid.token.here";
        UUID userId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(tokenBlacklist.isBlacklisted(token)).thenReturn(false);
        when(jwtProvider.getUserIdFromToken(token)).thenReturn(userId.toString());
        when(jwtProvider.getTenantIdFromToken(token)).thenReturn(tenantId.toString());
        when(jwtProvider.getTenantSlugFromToken(token)).thenReturn("acme");
        when(jwtProvider.getEmailFromToken(token)).thenReturn("user@acme.com");
        when(jwtProvider.getRolesFromToken(token)).thenReturn(List.of("USER", "ADMIN"));

        IntrospectResponse response = useCase.introspect(token);

        assertTrue(response.active());
        assertEquals(userId, response.userId());
        assertEquals(tenantId, response.tenantId());
        assertEquals("acme", response.tenantSlug());
        assertEquals("user@acme.com", response.email());
        assertEquals(List.of("USER", "ADMIN"), response.roles());
    }

    @Test
    @DisplayName("introspect should return inactive response for invalid token")
    void introspect_shouldReturnInactiveResponse_forInvalidToken() {
        String token = "invalid.token.here";

        when(jwtProvider.validateToken(token)).thenReturn(false);

        IntrospectResponse response = useCase.introspect(token);

        assertFalse(response.active());
        assertNull(response.userId());
        verify(tokenBlacklist, never()).isBlacklisted(anyString());
    }

    @Test
    @DisplayName("introspect should return inactive response for blacklisted token")
    void introspect_shouldReturnInactiveResponse_forBlacklistedToken() {
        String token = "blacklisted.token.here";

        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(tokenBlacklist.isBlacklisted(token)).thenReturn(true);

        IntrospectResponse response = useCase.introspect(token);

        assertFalse(response.active());
        assertNull(response.userId());
    }

    @Test
    @DisplayName("introspect should return inactive response when parsing fails")
    void introspect_shouldReturnInactiveResponse_whenParsingFails() {
        String token = "valid.but.unparseable";

        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(tokenBlacklist.isBlacklisted(token)).thenReturn(false);
        when(jwtProvider.getUserIdFromToken(token)).thenReturn("not-a-uuid");

        IntrospectResponse response = useCase.introspect(token);

        assertFalse(response.active());
    }
}
