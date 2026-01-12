package dev.ivanhernandez.authservice.domain.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User {

    private UUID id;
    private Tenant tenant;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private boolean emailVerified;
    private boolean enabled;
    private Set<Role> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(UUID id, Tenant tenant, String email, String passwordHash,
                String firstName, String lastName, boolean emailVerified,
                boolean enabled, Set<Role> roles, LocalDateTime createdAt,
                LocalDateTime updatedAt) {
        this.id = id;
        this.tenant = tenant;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailVerified = emailVerified;
        this.enabled = enabled;
        this.roles = roles != null ? roles : new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User create(Tenant tenant, String email, String passwordHash,
                              String firstName, String lastName) {
        Set<Role> defaultRoles = new HashSet<>();
        defaultRoles.add(Role.USER);

        return new User(
                null,
                tenant,
                email.toLowerCase(),
                passwordHash,
                firstName,
                lastName,
                false,
                true,
                defaultRoles,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public UUID getId() {
        return id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Set<Role> getRoles() {
        return Set.copyOf(roles);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void verifyEmail() {
        this.emailVerified = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfile(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.updatedAt = LocalDateTime.now();
    }

    public void addRole(Role role) {
        this.roles.add(role);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }

    public void disable() {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void enable() {
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
    }
}
