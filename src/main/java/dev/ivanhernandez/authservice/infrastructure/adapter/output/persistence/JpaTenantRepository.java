package dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence;

import dev.ivanhernandez.authservice.application.port.output.TenantRepository;
import dev.ivanhernandez.authservice.domain.model.Tenant;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity.TenantJpaEntity;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.spring.SpringDataTenantRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaTenantRepository implements TenantRepository {

    private final SpringDataTenantRepository springDataRepository;

    public JpaTenantRepository(SpringDataTenantRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Tenant save(Tenant tenant) {
        TenantJpaEntity entity = TenantJpaEntity.fromDomain(tenant);
        TenantJpaEntity saved = springDataRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Tenant> findById(UUID id) {
        return springDataRepository.findById(id)
                .map(TenantJpaEntity::toDomain);
    }

    @Override
    public Optional<Tenant> findBySlug(String slug) {
        return springDataRepository.findBySlug(slug)
                .map(TenantJpaEntity::toDomain);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return springDataRepository.existsBySlug(slug);
    }
}
