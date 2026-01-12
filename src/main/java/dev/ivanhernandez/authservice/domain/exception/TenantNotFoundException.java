package dev.ivanhernandez.authservice.domain.exception;

public class TenantNotFoundException extends RuntimeException {

    public TenantNotFoundException(String slug) {
        super("Tenant not found with slug: " + slug);
    }
}
