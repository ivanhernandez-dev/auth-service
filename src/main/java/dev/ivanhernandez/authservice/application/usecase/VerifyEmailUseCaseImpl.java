package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.port.input.VerifyEmailUseCase;
import dev.ivanhernandez.authservice.application.port.output.EmailSender;
import dev.ivanhernandez.authservice.application.port.output.EmailVerificationTokenRepository;
import dev.ivanhernandez.authservice.application.port.output.UserRepository;
import dev.ivanhernandez.authservice.domain.exception.InvalidTokenException;
import dev.ivanhernandez.authservice.domain.exception.TokenExpiredException;
import dev.ivanhernandez.authservice.domain.exception.UserNotFoundException;
import dev.ivanhernandez.authservice.domain.model.EmailVerificationToken;
import dev.ivanhernandez.authservice.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerifyEmailUseCaseImpl implements VerifyEmailUseCase {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailSender emailSender;

    public VerifyEmailUseCaseImpl(EmailVerificationTokenRepository tokenRepository,
                                  UserRepository userRepository,
                                  EmailSender emailSender) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.emailSender = emailSender;
    }

    @Override
    @Transactional
    public void verify(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(InvalidTokenException::new);

        if (verificationToken.isUsed()) {
            throw new InvalidTokenException("Token has already been used");
        }

        if (verificationToken.isExpired()) {
            throw new TokenExpiredException();
        }

        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.verifyEmail();
        userRepository.save(user);

        verificationToken.markAsUsed();
        tokenRepository.save(verificationToken);

        emailSender.sendWelcomeEmail(user.getEmail(), user.getFirstName());
    }
}
