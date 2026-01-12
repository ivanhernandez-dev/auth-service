package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.response.TokenResponse;
import dev.ivanhernandez.authservice.application.port.input.RefreshTokenUseCase;
import dev.ivanhernandez.authservice.application.port.output.JwtProvider;
import dev.ivanhernandez.authservice.application.port.output.RefreshTokenRepository;
import dev.ivanhernandez.authservice.application.port.output.UserRepository;
import dev.ivanhernandez.authservice.domain.exception.InvalidTokenException;
import dev.ivanhernandez.authservice.domain.exception.TokenExpiredException;
import dev.ivanhernandez.authservice.domain.exception.TokenRevokedException;
import dev.ivanhernandez.authservice.domain.exception.UserNotFoundException;
import dev.ivanhernandez.authservice.domain.model.RefreshToken;
import dev.ivanhernandez.authservice.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public RefreshTokenUseCaseImpl(RefreshTokenRepository refreshTokenRepository,
                                   UserRepository userRepository,
                                   JwtProvider jwtProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse refresh(String refreshToken) {
        String tokenHash = hashToken(refreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(InvalidTokenException::new);

        if (storedToken.isRevoked()) {
            throw new TokenRevokedException();
        }

        if (storedToken.isExpired()) {
            throw new TokenExpiredException();
        }

        User user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String accessToken = jwtProvider.generateAccessToken(user);

        return TokenResponse.of(accessToken, jwtProvider.getAccessTokenExpirationMs());
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
