package dev.ivanhernandez.authservice.infrastructure.adapter.output.security;

import dev.ivanhernandez.authservice.application.port.output.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoder {

    private final BCryptPasswordEncoder encoder;

    public BCryptPasswordEncoderAdapter() {
        this.encoder = new BCryptPasswordEncoder(12);
    }

    @Override
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
