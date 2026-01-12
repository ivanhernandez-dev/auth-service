package dev.ivanhernandez.authservice.infrastructure.adapter.output.security;

import dev.ivanhernandez.authservice.application.port.output.JwtProvider;
import dev.ivanhernandez.authservice.domain.model.Role;
import dev.ivanhernandez.authservice.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtProviderImpl implements JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;

    public JwtProviderImpl(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-ms:900000}") long accessTokenExpirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }

    @Override
    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("tenantId", user.getTenant().getId().toString())
                .claim("tenantSlug", user.getTenant().getSlug())
                .claim("roles", user.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.toList()))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    @Override
    public String getTenantIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("tenantId", String.class);
    }

    @Override
    public String getTenantSlugFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("tenantSlug", String.class);
    }

    @Override
    public String getEmailFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("email", String.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public java.util.List<String> getRolesFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("roles", java.util.List.class);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }

    @Override
    public long getRemainingExpirationSeconds(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            long remainingMs = expiration.getTime() - System.currentTimeMillis();
            return Math.max(0, remainingMs / 1000);
        } catch (JwtException | IllegalArgumentException e) {
            return 0;
        }
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
