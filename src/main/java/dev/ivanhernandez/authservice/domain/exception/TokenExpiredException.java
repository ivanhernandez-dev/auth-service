package dev.ivanhernandez.authservice.domain.exception;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException() {
        super("Token has expired");
    }
}
