package dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity;

import dev.ivanhernandez.authservice.domain.model.Role;
import dev.ivanhernandez.authservice.domain.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "email"})
})
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private TenantJpaEntity tenant;

    @Column(nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(nullable = false)
    private boolean enabled;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UserJpaEntity() {
    }

    public static UserJpaEntity fromDomain(User user, TenantJpaEntity tenantEntity) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.id = user.getId();
        entity.tenant = tenantEntity;
        entity.email = user.getEmail();
        entity.passwordHash = user.getPasswordHash();
        entity.firstName = user.getFirstName();
        entity.lastName = user.getLastName();
        entity.emailVerified = user.isEmailVerified();
        entity.enabled = user.isEnabled();
        entity.roles = new HashSet<>(user.getRoles());
        entity.createdAt = user.getCreatedAt();
        entity.updatedAt = user.getUpdatedAt();
        return entity;
    }

    public User toDomain() {
        return new User(
                id,
                tenant.toDomain(),
                email,
                passwordHash,
                firstName,
                lastName,
                emailVerified,
                enabled,
                new HashSet<>(roles),
                createdAt,
                updatedAt
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TenantJpaEntity getTenant() {
        return tenant;
    }

    public void setTenant(TenantJpaEntity tenant) {
        this.tenant = tenant;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
