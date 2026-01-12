package dev.ivanhernandez.authservice.domain.exception;

public class TokenRevokedException extends RuntimeException {

    public TokenRevokedException() {
        super("Token has been revoked");
    }
}
