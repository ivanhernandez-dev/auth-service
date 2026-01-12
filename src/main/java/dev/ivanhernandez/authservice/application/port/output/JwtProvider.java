package dev.ivanhernandez.authservice.application.port.output;

import dev.ivanhernandez.authservice.domain.model.User;

import java.util.List;

public interface JwtProvider {

    String generateAccessToken(User user);

    String getUserIdFromToken(String token);

    String getTenantIdFromToken(String token);

    String getTenantSlugFromToken(String token);

    String getEmailFromToken(String token);

    List<String> getRolesFromToken(String token);

    boolean validateToken(String token);

    long getAccessTokenExpirationMs();

    long getRemainingExpirationSeconds(String token);
}
