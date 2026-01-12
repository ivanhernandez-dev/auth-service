package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.response.TenantResponse;
import dev.ivanhernandez.authservice.application.port.input.GetTenantUseCase;
import dev.ivanhernandez.authservice.application.port.output.TenantRepository;
import dev.ivanhernandez.authservice.domain.exception.TenantNotFoundException;
import dev.ivanhernandez.authservice.domain.model.Tenant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetTenantUseCaseImpl implements GetTenantUseCase {

    private final TenantRepository tenantRepository;

    public GetTenantUseCaseImpl(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public TenantResponse getBySlug(String slug) {
        Tenant tenant = tenantRepository.findBySlug(slug)
                .orElseThrow(() -> new TenantNotFoundException(slug));

        return TenantResponse.fromDomain(tenant);
    }
}
