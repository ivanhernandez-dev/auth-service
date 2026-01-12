package dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence;

import dev.ivanhernandez.authservice.domain.model.Role;
import dev.ivanhernandez.authservice.domain.model.Tenant;
import dev.ivanhernandez.authservice.domain.model.User;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity.TenantJpaEntity;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.spring.SpringDataTenantRepository;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.spring.SpringDataUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import({JpaUserRepository.class, JpaTenantRepository.class})
class JpaUserRepositoryIntegrationTest {

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private SpringDataTenantRepository springDataTenantRepository;

    @Autowired
    private SpringDataUserRepository springDataUserRepository;

    private Tenant tenant;

    @BeforeEach
    void setUp() {
        TenantJpaEntity tenantEntity = new TenantJpaEntity();
        tenantEntity.setName("Test Corp");
        tenantEntity.setSlug("test-corp");
        tenantEntity.setEnabled(true);
        tenantEntity.setCreatedAt(LocalDateTime.now());

        TenantJpaEntity saved = springDataTenantRepository.save(tenantEntity);
        tenant = saved.toDomain();
    }

    @Test
    @DisplayName("save should persist user")
    void save_shouldPersistUser() {
        User user = new User(
                null,
                tenant,
                "test@test.com",
                "hashedPassword",
                "Test",
                "User",
                false,
                true,
                Set.of(Role.USER),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("test@test.com", saved.getEmail());
    }

    @Test
    @DisplayName("findByEmailAndTenantId should return user")
    void findByEmailAndTenantId_shouldReturnUser() {
        User user = new User(
                null,
                tenant,
                "find@test.com",
                "hashedPassword",
                "Find",
                "User",
                false,
                true,
                Set.of(Role.USER),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmailAndTenantId("find@test.com", tenant.getId());

        assertTrue(found.isPresent());
        assertEquals("find@test.com", found.get().getEmail());
    }

    @Test
    @DisplayName("findByEmailAndTenantSlug should return user")
    void findByEmailAndTenantSlug_shouldReturnUser() {
        User user = new User(
                null,
                tenant,
                "slug@test.com",
                "hashedPassword",
                "Slug",
                "User",
                false,
                true,
                Set.of(Role.USER),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmailAndTenantSlug("slug@test.com", "test-corp");

        assertTrue(found.isPresent());
        assertEquals("slug@test.com", found.get().getEmail());
    }

    @Test
    @DisplayName("existsByEmailAndTenantId should return true when exists")
    void existsByEmailAndTenantId_shouldReturnTrue_whenExists() {
        User user = new User(
                null,
                tenant,
                "exists@test.com",
                "hashedPassword",
                "Exists",
                "User",
                false,
                true,
                Set.of(Role.USER),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        userRepository.save(user);

        boolean exists = userRepository.existsByEmailAndTenantId("exists@test.com", tenant.getId());

        assertTrue(exists);
    }

    @Test
    @DisplayName("existsByEmailAndTenantId should return false when not exists")
    void existsByEmailAndTenantId_shouldReturnFalse_whenNotExists() {
        boolean exists = userRepository.existsByEmailAndTenantId("notexists@test.com", tenant.getId());

        assertFalse(exists);
    }
}
