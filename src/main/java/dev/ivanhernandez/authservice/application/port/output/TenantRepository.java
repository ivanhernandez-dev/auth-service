package dev.ivanhernandez.authservice.application.port.output;

import dev.ivanhernandez.authservice.domain.model.Tenant;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository {

    Tenant save(Tenant tenant);

    Optional<Tenant> findById(UUID id);

    Optional<Tenant> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
