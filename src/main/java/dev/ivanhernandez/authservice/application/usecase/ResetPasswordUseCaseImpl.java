package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.request.ResetPasswordRequest;
import dev.ivanhernandez.authservice.application.port.input.ResetPasswordUseCase;
import dev.ivanhernandez.authservice.application.port.output.PasswordEncoder;
import dev.ivanhernandez.authservice.application.port.output.PasswordResetTokenRepository;
import dev.ivanhernandez.authservice.application.port.output.RefreshTokenRepository;
import dev.ivanhernandez.authservice.application.port.output.UserRepository;
import dev.ivanhernandez.authservice.domain.exception.InvalidTokenException;
import dev.ivanhernandez.authservice.domain.exception.TokenExpiredException;
import dev.ivanhernandez.authservice.domain.exception.UserNotFoundException;
import dev.ivanhernandez.authservice.domain.model.PasswordResetToken;
import dev.ivanhernandez.authservice.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResetPasswordUseCaseImpl implements ResetPasswordUseCase {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordUseCaseImpl(PasswordResetTokenRepository tokenRepository,
                                    UserRepository userRepository,
                                    RefreshTokenRepository refreshTokenRepository,
                                    PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void reset(ResetPasswordRequest request) {
        PasswordResetToken resetToken = tokenRepository.findByToken(request.token())
                .orElseThrow(InvalidTokenException::new);

        if (resetToken.isUsed()) {
            throw new InvalidTokenException("Token has already been used");
        }

        if (resetToken.isExpired()) {
            throw new TokenExpiredException();
        }

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String newPasswordHash = passwordEncoder.encode(request.newPassword());
        user.updatePassword(newPasswordHash);
        userRepository.save(user);

        resetToken.markAsUsed();
        tokenRepository.save(resetToken);

        refreshTokenRepository.revokeAllByUserId(user.getId());
    }
}
