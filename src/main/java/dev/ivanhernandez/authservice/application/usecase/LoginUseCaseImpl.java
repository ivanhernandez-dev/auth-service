package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.request.LoginRequest;
import dev.ivanhernandez.authservice.application.dto.response.AuthResponse;
import dev.ivanhernandez.authservice.application.dto.response.UserProfileResponse;
import dev.ivanhernandez.authservice.application.port.input.LoginUseCase;
import dev.ivanhernandez.authservice.application.port.output.*;
import dev.ivanhernandez.authservice.domain.exception.InvalidCredentialsException;
import dev.ivanhernandez.authservice.domain.exception.TenantDisabledException;
import dev.ivanhernandez.authservice.domain.exception.UserDisabledException;
import dev.ivanhernandez.authservice.domain.exception.UserNotVerifiedException;
import dev.ivanhernandez.authservice.domain.model.LoginAttempt;
import dev.ivanhernandez.authservice.domain.model.RefreshToken;
import dev.ivanhernandez.authservice.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class LoginUseCaseImpl implements LoginUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenGenerator tokenGenerator;

    public LoginUseCaseImpl(UserRepository userRepository,
                            RefreshTokenRepository refreshTokenRepository,
                            LoginAttemptRepository loginAttemptRepository,
                            PasswordEncoder passwordEncoder,
                            JwtProvider jwtProvider,
                            TokenGenerator tokenGenerator) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.loginAttemptRepository = loginAttemptRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        User user = userRepository.findByEmailAndTenantSlug(request.email().toLowerCase(), request.tenantSlug())
                .orElseThrow(() -> {
                    recordFailedAttempt(request, ipAddress, userAgent);
                    return new InvalidCredentialsException();
                });

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            recordFailedAttempt(request, ipAddress, userAgent);
            throw new InvalidCredentialsException();
        }

        if (!user.getTenant().isEnabled()) {
            throw new TenantDisabledException(request.tenantSlug());
        }

        if (!user.isEnabled()) {
            throw new UserDisabledException(request.email());
        }

        if (!user.isEmailVerified()) {
            throw new UserNotVerifiedException(request.email());
        }

        recordSuccessfulAttempt(user, request, ipAddress, userAgent);

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = tokenGenerator.generateSecureToken(32);
        String refreshTokenHash = hashToken(refreshToken);

        RefreshToken tokenEntity = RefreshToken.create(user.getId(), refreshTokenHash);
        refreshTokenRepository.save(tokenEntity);

        return AuthResponse.of(
                accessToken,
                refreshToken,
                jwtProvider.getAccessTokenExpirationMs(),
                UserProfileResponse.fromDomain(user)
        );
    }

    private void recordFailedAttempt(LoginRequest request, String ipAddress, String userAgent) {
        LoginAttempt attempt = LoginAttempt.failure(
                request.email(),
                request.tenantSlug(),
                ipAddress,
                userAgent
        );
        loginAttemptRepository.save(attempt);
    }

    private void recordSuccessfulAttempt(User user, LoginRequest request, String ipAddress, String userAgent) {
        LoginAttempt attempt = LoginAttempt.success(
                user.getId(),
                request.email(),
                request.tenantSlug(),
                ipAddress,
                userAgent
        );
        loginAttemptRepository.save(attempt);
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
