package dev.ivanhernandez.authservice.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Tenant {

    private UUID id;
    private String name;
    private String slug;
    private boolean enabled;
    private LocalDateTime createdAt;

    public Tenant(UUID id, String name, String slug, boolean enabled, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }

    public static Tenant create(String name, String slug) {
        return new Tenant(
                null,
                name,
                slug.toLowerCase(),
                true,
                LocalDateTime.now()
        );
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void disable() {
        this.enabled = false;
    }

    public void enable() {
        this.enabled = true;
    }
}
