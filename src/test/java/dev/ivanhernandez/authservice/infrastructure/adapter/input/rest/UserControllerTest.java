package dev.ivanhernandez.authservice.infrastructure.adapter.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ivanhernandez.authservice.application.dto.request.ChangePasswordRequest;
import dev.ivanhernandez.authservice.application.dto.request.UpdateUserProfileRequest;
import dev.ivanhernandez.authservice.application.dto.response.UserProfileResponse;
import dev.ivanhernandez.authservice.application.port.input.ChangePasswordUseCase;
import dev.ivanhernandez.authservice.application.port.input.GetUserProfileUseCase;
import dev.ivanhernandez.authservice.application.port.input.UpdateUserProfileUseCase;
import dev.ivanhernandez.authservice.application.port.output.JwtProvider;
import dev.ivanhernandez.authservice.application.port.output.RateLimiter;
import dev.ivanhernandez.authservice.application.port.output.TokenBlacklist;
import dev.ivanhernandez.authservice.domain.exception.InvalidCredentialsException;
import dev.ivanhernandez.authservice.infrastructure.config.RateLimitingFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = RateLimitingFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetUserProfileUseCase getUserProfileUseCase;
    @MockBean
    private UpdateUserProfileUseCase updateUserProfileUseCase;
    @MockBean
    private ChangePasswordUseCase changePasswordUseCase;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private RateLimiter rateLimiter;
    @MockBean
    private TokenBlacklist tokenBlacklist;

    private final UUID userId = UUID.randomUUID();

    private UserProfileResponse createUserProfileResponse() {
        return new UserProfileResponse(
                userId,
                "john@acme.com",
                "John",
                "Doe",
                "acme",
                "ACME",
                List.of("USER"),
                true,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("GET /users/me should return user profile")
    void getProfile_shouldReturnUserProfile() throws Exception {
        UserProfileResponse response = createUserProfileResponse();

        when(jwtProvider.validateToken(any())).thenReturn(true);
        when(jwtProvider.getUserIdFromToken(any())).thenReturn(userId.toString());
        when(getUserProfileUseCase.getProfile(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/me")
                        .with(user(userId.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@acme.com"));
    }

    @Test
    @DisplayName("PUT /users/me should update profile")
    void updateProfile_shouldUpdateProfile() throws Exception {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest("Jane", "Smith");
        UserProfileResponse response = new UserProfileResponse(
                userId,
                "john@acme.com",
                "Jane",
                "Smith",
                "acme",
                "ACME",
                List.of("USER"),
                true,
                LocalDateTime.now()
        );

        when(jwtProvider.validateToken(any())).thenReturn(true);
        when(jwtProvider.getUserIdFromToken(any())).thenReturn(userId.toString());
        when(updateUserProfileUseCase.update(any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/me")
                        .with(user(userId.toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    @DisplayName("PUT /users/me/password should change password")
    void changePassword_shouldChangePassword() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("OldPass1!", "NewPass1!");

        when(jwtProvider.validateToken(any())).thenReturn(true);
        when(jwtProvider.getUserIdFromToken(any())).thenReturn(userId.toString());
        doNothing().when(changePasswordUseCase).changePassword(any(), any());

        mockMvc.perform(put("/api/v1/users/me/password")
                        .with(user(userId.toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));
    }

    @Test
    @DisplayName("PUT /users/me/password should return 401 for wrong current password")
    void changePassword_shouldReturn401_forWrongCurrentPassword() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("WrongPass1!", "NewPass1!");

        when(jwtProvider.validateToken(any())).thenReturn(true);
        when(jwtProvider.getUserIdFromToken(any())).thenReturn(userId.toString());
        doThrow(new InvalidCredentialsException()).when(changePasswordUseCase).changePassword(any(), any());

        mockMvc.perform(put("/api/v1/users/me/password")
                        .with(user(userId.toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
