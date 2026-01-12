package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.port.input.LogoutUseCase;
import dev.ivanhernandez.authservice.application.port.output.JwtProvider;
import dev.ivanhernandez.authservice.application.port.output.RefreshTokenRepository;
import dev.ivanhernandez.authservice.application.port.output.TokenBlacklist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Service
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklist tokenBlacklist;
    private final JwtProvider jwtProvider;

    public LogoutUseCaseImpl(RefreshTokenRepository refreshTokenRepository,
                             TokenBlacklist tokenBlacklist,
                             JwtProvider jwtProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenBlacklist = tokenBlacklist;
        this.jwtProvider = jwtProvider;
    }

    @Override
    @Transactional
    public void logout(UUID userId, String accessToken, String refreshToken) {
        blacklistAccessToken(accessToken);
        String tokenHash = hashToken(refreshToken);
        refreshTokenRepository.revokeByTokenHash(tokenHash);
    }

    @Override
    @Transactional
    public void logoutAllDevices(UUID userId, String accessToken) {
        blacklistAccessToken(accessToken);
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    private void blacklistAccessToken(String accessToken) {
        if (accessToken != null) {
            long remainingSeconds = jwtProvider.getRemainingExpirationSeconds(accessToken);
            if (remainingSeconds > 0) {
                tokenBlacklist.blacklist(accessToken, remainingSeconds);
            }
        }
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
