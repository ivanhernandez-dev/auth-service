package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.request.RequestPasswordResetRequest;
import dev.ivanhernandez.authservice.application.port.input.RequestPasswordResetUseCase;
import dev.ivanhernandez.authservice.application.port.output.EmailSender;
import dev.ivanhernandez.authservice.application.port.output.PasswordResetTokenRepository;
import dev.ivanhernandez.authservice.application.port.output.TokenGenerator;
import dev.ivanhernandez.authservice.application.port.output.UserRepository;
import dev.ivanhernandez.authservice.domain.model.PasswordResetToken;
import dev.ivanhernandez.authservice.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RequestPasswordResetUseCaseImpl implements RequestPasswordResetUseCase {

    private static final Logger log = LoggerFactory.getLogger(RequestPasswordResetUseCaseImpl.class);

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final TokenGenerator tokenGenerator;
    private final EmailSender emailSender;

    public RequestPasswordResetUseCaseImpl(UserRepository userRepository,
                                           PasswordResetTokenRepository tokenRepository,
                                           TokenGenerator tokenGenerator,
                                           EmailSender emailSender) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.tokenGenerator = tokenGenerator;
        this.emailSender = emailSender;
    }

    @Override
    @Transactional
    public void requestReset(RequestPasswordResetRequest request) {
        Optional<User> userOptional = userRepository.findByEmailAndTenantSlug(
                request.email().toLowerCase(),
                request.tenantSlug()
        );

        if (userOptional.isEmpty()) {
            log.info("Password reset requested for non-existent user: {}", request.email());
            return;
        }

        User user = userOptional.get();

        tokenRepository.deleteByUserId(user.getId());

        String token = tokenGenerator.generateSecureToken(32);
        PasswordResetToken resetToken = PasswordResetToken.create(user.getId(), token);
        tokenRepository.save(resetToken);

        emailSender.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), token);
    }
}
