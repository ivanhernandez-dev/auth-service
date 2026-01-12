package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.request.RegisterRequest;
import dev.ivanhernandez.authservice.application.dto.response.UserProfileResponse;
import dev.ivanhernandez.authservice.application.port.input.RegisterUserUseCase;
import dev.ivanhernandez.authservice.application.port.output.*;
import dev.ivanhernandez.authservice.domain.exception.TenantDisabledException;
import dev.ivanhernandez.authservice.domain.exception.TenantNotFoundException;
import dev.ivanhernandez.authservice.domain.exception.UserAlreadyExistsException;
import dev.ivanhernandez.authservice.domain.model.EmailVerificationToken;
import dev.ivanhernandez.authservice.domain.model.Tenant;
import dev.ivanhernandez.authservice.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final EmailVerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;
    private final EmailSender emailSender;

    public RegisterUserUseCaseImpl(UserRepository userRepository,
                                   TenantRepository tenantRepository,
                                   EmailVerificationTokenRepository verificationTokenRepository,
                                   PasswordEncoder passwordEncoder,
                                   TokenGenerator tokenGenerator,
                                   EmailSender emailSender) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.emailSender = emailSender;
    }

    @Override
    @Transactional
    public UserProfileResponse register(RegisterRequest request) {
        Tenant tenant = tenantRepository.findBySlug(request.tenantSlug())
                .orElseThrow(() -> new TenantNotFoundException(request.tenantSlug()));

        if (!tenant.isEnabled()) {
            throw new TenantDisabledException(request.tenantSlug());
        }

        if (userRepository.existsByEmailAndTenantId(request.email().toLowerCase(), tenant.getId())) {
            throw new UserAlreadyExistsException(request.email());
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = User.create(
                tenant,
                request.email(),
                passwordHash,
                request.firstName(),
                request.lastName()
        );

        User savedUser = userRepository.save(user);

        String token = tokenGenerator.generateSecureToken(32);
        EmailVerificationToken verificationToken = EmailVerificationToken.create(savedUser.getId(), token);
        verificationTokenRepository.save(verificationToken);

        emailSender.sendVerificationEmail(savedUser.getEmail(), savedUser.getFirstName(), token);

        return UserProfileResponse.fromDomain(savedUser);
    }
}
