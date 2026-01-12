package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.request.ChangePasswordRequest;
import dev.ivanhernandez.authservice.application.port.input.ChangePasswordUseCase;
import dev.ivanhernandez.authservice.application.port.output.PasswordEncoder;
import dev.ivanhernandez.authservice.application.port.output.RefreshTokenRepository;
import dev.ivanhernandez.authservice.application.port.output.UserRepository;
import dev.ivanhernandez.authservice.domain.exception.InvalidCredentialsException;
import dev.ivanhernandez.authservice.domain.exception.UserNotFoundException;
import dev.ivanhernandez.authservice.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ChangePasswordUseCaseImpl implements ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordUseCaseImpl(UserRepository userRepository,
                                     RefreshTokenRepository refreshTokenRepository,
                                     PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String newPasswordHash = passwordEncoder.encode(request.newPassword());
        user.updatePassword(newPasswordHash);
        userRepository.save(user);

        refreshTokenRepository.revokeAllByUserId(userId);
    }
}
