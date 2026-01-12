package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.request.CreateTenantRequest;
import dev.ivanhernandez.authservice.application.dto.response.TenantResponse;
import dev.ivanhernandez.authservice.application.port.output.TenantRepository;
import dev.ivanhernandez.authservice.domain.model.Tenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTenantUseCaseImplTest {

    @Mock
    private TenantRepository tenantRepository;

    private CreateTenantUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateTenantUseCaseImpl(tenantRepository);
    }

    @Test
    @DisplayName("create should create new tenant")
    void create_shouldCreateNewTenant() {
        CreateTenantRequest request = new CreateTenantRequest("ACME Corporation", "acme");
        Tenant savedTenant = new Tenant(UUID.randomUUID(), "ACME Corporation", "acme", true, LocalDateTime.now());

        when(tenantRepository.existsBySlug("acme")).thenReturn(false);
        when(tenantRepository.save(any())).thenReturn(savedTenant);

        TenantResponse response = useCase.create(request);

        assertNotNull(response);
        assertEquals("ACME Corporation", response.name());
        assertEquals("acme", response.slug());
        assertTrue(response.enabled());
    }

    @Test
    @DisplayName("create should throw exception when slug exists")
    void create_shouldThrowException_whenSlugExists() {
        CreateTenantRequest request = new CreateTenantRequest("ACME Corporation", "acme");

        when(tenantRepository.existsBySlug("acme")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> useCase.create(request));
    }
}
