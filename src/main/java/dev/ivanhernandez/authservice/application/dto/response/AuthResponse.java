package dev.ivanhernandez.authservice.application.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserProfileResponse user
) {
    public static AuthResponse of(String accessToken, String refreshToken, long expiresInMs, UserProfileResponse user) {
        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                expiresInMs / 1000,
                user
        );
    }
}
