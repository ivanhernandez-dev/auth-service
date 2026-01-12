package dev.ivanhernandez.authservice.application.port.output;

import dev.ivanhernandez.authservice.domain.model.User;

public interface JwtProvider {

    String generateAccessToken(User user);

    String getUserIdFromToken(String token);

    String getTenantIdFromToken(String token);

    boolean validateToken(String token);

    long getAccessTokenExpirationMs();

    long getRemainingExpirationSeconds(String token);
}
