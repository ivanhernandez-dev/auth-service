package dev.ivanhernandez.authservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("create should create user with default role USER")
    void create_shouldCreateUserWithDefaultRoleUser() {
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());

        User user = User.create(tenant, "test@example.com", "hashedPassword", "John", "Doe");

        assertNotNull(user);
        assertEquals("test@example.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertFalse(user.isEmailVerified());
        assertTrue(user.isEnabled());
        assertTrue(user.getRoles().contains(Role.USER));
        assertEquals(1, user.getRoles().size());
    }

    @Test
    @DisplayName("create should lowercase email")
    void create_shouldLowercaseEmail() {
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());

        User user = User.create(tenant, "TEST@EXAMPLE.COM", "hashedPassword", "John", "Doe");

        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    @DisplayName("verifyEmail should set emailVerified to true")
    void verifyEmail_shouldSetEmailVerifiedToTrue() {
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());
        User user = User.create(tenant, "test@example.com", "hashedPassword", "John", "Doe");

        assertFalse(user.isEmailVerified());

        user.verifyEmail();

        assertTrue(user.isEmailVerified());
    }

    @Test
    @DisplayName("updateProfile should update firstName and lastName")
    void updateProfile_shouldUpdateFirstNameAndLastName() {
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());
        User user = User.create(tenant, "test@example.com", "hashedPassword", "John", "Doe");

        user.updateProfile("Jane", "Smith");

        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
    }

    @Test
    @DisplayName("addRole should add role to user")
    void addRole_shouldAddRoleToUser() {
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());
        User user = User.create(tenant, "test@example.com", "hashedPassword", "John", "Doe");

        user.addRole(Role.ADMIN);

        assertTrue(user.hasRole(Role.ADMIN));
        assertTrue(user.hasRole(Role.USER));
        assertEquals(2, user.getRoles().size());
    }

    @Test
    @DisplayName("disable should set enabled to false")
    void disable_shouldSetEnabledToFalse() {
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());
        User user = User.create(tenant, "test@example.com", "hashedPassword", "John", "Doe");

        assertTrue(user.isEnabled());

        user.disable();

        assertFalse(user.isEnabled());
    }

    @Test
    @DisplayName("getFullName should return first and last name")
    void getFullName_shouldReturnFirstAndLastName() {
        Tenant tenant = new Tenant(UUID.randomUUID(), "ACME", "acme", true, LocalDateTime.now());
        User user = User.create(tenant, "test@example.com", "hashedPassword", "John", "Doe");

        assertEquals("John Doe", user.getFullName());
    }
}
