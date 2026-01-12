package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.response.TenantResponse;
import dev.ivanhernandez.authservice.application.port.output.TenantRepository;
import dev.ivanhernandez.authservice.domain.exception.TenantNotFoundException;
import dev.ivanhernandez.authservice.domain.model.Tenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTenantUseCaseImplTest {

    @Mock
    private TenantRepository tenantRepository;

    private GetTenantUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetTenantUseCaseImpl(tenantRepository);
    }

    @Test
    @DisplayName("getBySlug should return tenant")
    void getBySlug_shouldReturnTenant() {
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME Corporation", "acme", true, LocalDateTime.now());

        when(tenantRepository.findBySlug("acme")).thenReturn(Optional.of(tenant));

        TenantResponse response = useCase.getBySlug("acme");

        assertNotNull(response);
        assertEquals("ACME Corporation", response.name());
        assertEquals("acme", response.slug());
    }

    @Test
    @DisplayName("getBySlug should throw TenantNotFoundException when not found")
    void getBySlug_shouldThrowTenantNotFoundException_whenNotFound() {
        when(tenantRepository.findBySlug("unknown")).thenReturn(Optional.empty());

        assertThrows(TenantNotFoundException.class, () -> useCase.getBySlug("unknown"));
    }
}
