package dev.ivanhernandez.authservice.application.usecase;

import dev.ivanhernandez.authservice.application.dto.request.UpdateUserProfileRequest;
import dev.ivanhernandez.authservice.application.dto.response.UserProfileResponse;
import dev.ivanhernandez.authservice.application.port.input.UpdateUserProfileUseCase;
import dev.ivanhernandez.authservice.application.port.output.UserRepository;
import dev.ivanhernandez.authservice.domain.exception.UserNotFoundException;
import dev.ivanhernandez.authservice.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateUserProfileUseCaseImpl implements UpdateUserProfileUseCase {

    private final UserRepository userRepository;

    public UpdateUserProfileUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserProfileResponse update(UUID userId, UpdateUserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.updateProfile(request.firstName(), request.lastName());
        User savedUser = userRepository.save(user);

        return UserProfileResponse.fromDomain(savedUser);
    }
}
