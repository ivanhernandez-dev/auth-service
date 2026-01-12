package dev.ivanhernandez.authservice.application.port.output;

import dev.ivanhernandez.authservice.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmailAndTenantId(String email, UUID tenantId);

    Optional<User> findByEmailAndTenantSlug(String email, String tenantSlug);

    boolean existsByEmailAndTenantId(String email, UUID tenantId);
}
