package dev.ivanhernandez.authservice.application.dto.response;

public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
    public static TokenResponse of(String accessToken, long expiresInMs) {
        return new TokenResponse(
                accessToken,
                "Bearer",
                expiresInMs / 1000
        );
    }
}
