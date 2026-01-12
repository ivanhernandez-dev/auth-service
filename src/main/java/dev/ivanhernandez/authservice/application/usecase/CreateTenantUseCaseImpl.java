package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.request.CreateTenantRequest;
import dev.ivanhernandez.authservice.application.dto.response.TenantResponse;
import dev.ivanhernandez.authservice.application.port.input.CreateTenantUseCase;
import dev.ivanhernandez.authservice.application.port.output.TenantRepository;
import dev.ivanhernandez.authservice.domain.model.Tenant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateTenantUseCaseImpl implements CreateTenantUseCase {

    private final TenantRepository tenantRepository;

    public CreateTenantUseCaseImpl(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    @Transactional
    public TenantResponse create(CreateTenantRequest request) {
        if (tenantRepository.existsBySlug(request.slug())) {
            throw new IllegalArgumentException("Tenant with slug '" + request.slug() + "' already exists");
        }

        Tenant tenant = Tenant.create(request.name(), request.slug());
        Tenant savedTenant = tenantRepository.save(tenant);

        return TenantResponse.fromDomain(savedTenant);
    }
}
