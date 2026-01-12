package dev.ivanhernandez.authservice.domain.exception;

public class UserDisabledException extends RuntimeException {

    public UserDisabledException(String email) {
        super("User account is disabled: " + email);
    }
}
