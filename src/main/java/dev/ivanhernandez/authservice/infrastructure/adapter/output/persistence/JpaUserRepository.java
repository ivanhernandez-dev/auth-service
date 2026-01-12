package dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence;

import dev.ivanhernandez.authservice.application.port.output.UserRepository;
import dev.ivanhernandez.authservice.domain.model.User;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity.TenantJpaEntity;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity.UserJpaEntity;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.spring.SpringDataTenantRepository;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.spring.SpringDataUserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserRepository springDataUserRepository;
    private final SpringDataTenantRepository springDataTenantRepository;

    public JpaUserRepository(SpringDataUserRepository springDataUserRepository,
                             SpringDataTenantRepository springDataTenantRepository) {
        this.springDataUserRepository = springDataUserRepository;
        this.springDataTenantRepository = springDataTenantRepository;
    }

    @Override
    public User save(User user) {
        TenantJpaEntity tenantEntity = springDataTenantRepository
                .findById(user.getTenant().getId())
                .orElseThrow(() -> new IllegalStateException("Tenant not found"));

        UserJpaEntity entity = UserJpaEntity.fromDomain(user, tenantEntity);
        UserJpaEntity saved = springDataUserRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springDataUserRepository.findById(id)
                .map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmailAndTenantId(String email, UUID tenantId) {
        return springDataUserRepository.findByEmailAndTenantId(email, tenantId)
                .map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmailAndTenantSlug(String email, String tenantSlug) {
        return springDataUserRepository.findByEmailAndTenantSlug(email, tenantSlug)
                .map(UserJpaEntity::toDomain);
    }

    @Override
    public boolean existsByEmailAndTenantId(String email, UUID tenantId) {
        return springDataUserRepository.existsByEmailAndTenantId(email, tenantId);
    }
}
