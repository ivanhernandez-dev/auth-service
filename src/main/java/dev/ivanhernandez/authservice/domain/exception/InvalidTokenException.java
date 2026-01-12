package dev.ivanhernandez.authservice.domain.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("Invalid token");
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}
