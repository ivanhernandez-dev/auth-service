package dev.ivanhernandez.authservice.domain.exception;

public class UserNotVerifiedException extends RuntimeException {

    public UserNotVerifiedException(String email) {
        super("Email not verified for user: " + email);
    }
}
