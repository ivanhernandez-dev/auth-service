package dev.ivanhernandez.authservice.infrastructure.adapter.output.security;

import dev.ivanhernandez.authservice.application.port.output.TokenGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Component
public class SecureTokenGenerator implements TokenGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int DEFAULT_TOKEN_LENGTH = 32;

    @Override
    public String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public String generateSecureToken(int length) {
        byte[] bytes = new byte[length];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
