package dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.spring;

import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmailAndTenantId(String email, UUID tenantId);

    @Query("SELECT u FROM UserJpaEntity u JOIN u.tenant t WHERE u.email = :email AND t.slug = :tenantSlug")
    Optional<UserJpaEntity> findByEmailAndTenantSlug(@Param("email") String email, @Param("tenantSlug") String tenantSlug);

    boolean existsByEmailAndTenantId(String email, UUID tenantId);
}
