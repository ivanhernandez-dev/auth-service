package dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity;

import dev.ivanhernandez.authservice.domain.model.Tenant;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenants")
public class TenantJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String slug;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public TenantJpaEntity() {
    }

    public static TenantJpaEntity fromDomain(Tenant tenant) {
        TenantJpaEntity entity = new TenantJpaEntity();
        entity.id = tenant.getId();
        entity.name = tenant.getName();
        entity.slug = tenant.getSlug();
        entity.enabled = tenant.isEnabled();
        entity.createdAt = tenant.getCreatedAt();
        return entity;
    }

    public Tenant toDomain() {
        return new Tenant(id, name, slug, enabled, createdAt);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
