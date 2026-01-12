package dev.ivanhernandez.authservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TenantTest {

    @Test
    @DisplayName("create should create enabled tenant with lowercase slug")
    void create_shouldCreateEnabledTenantWithLowercaseSlug() {
        Tenant tenant = Tenant.create("ACME Corporation", "ACME");

        assertNotNull(tenant);
        assertEquals("ACME Corporation", tenant.getName());
        assertEquals("acme", tenant.getSlug());
        assertTrue(tenant.isEnabled());
        assertNotNull(tenant.getCreatedAt());
    }

    @Test
    @DisplayName("disable should set enabled to false")
    void disable_shouldSetEnabledToFalse() {
        Tenant tenant = Tenant.create("ACME", "acme");

        assertTrue(tenant.isEnabled());

        tenant.disable();

        assertFalse(tenant.isEnabled());
    }

    @Test
    @DisplayName("enable should set enabled to true")
    void enable_shouldSetEnabledToTrue() {
        Tenant tenant = Tenant.create("ACME", "acme");
        tenant.disable();

        assertFalse(tenant.isEnabled());

        tenant.enable();

        assertTrue(tenant.isEnabled());
    }
}
