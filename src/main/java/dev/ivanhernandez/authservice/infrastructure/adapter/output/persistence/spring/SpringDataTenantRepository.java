package dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.spring;

import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity.TenantJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataTenantRepository extends JpaRepository<TenantJpaEntity, UUID> {

    Optional<TenantJpaEntity> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
