package dev.ivanhernandez.authservice.application.port.output;

public interface TokenGenerator {

    String generateToken();

    String generateSecureToken(int length);
}
