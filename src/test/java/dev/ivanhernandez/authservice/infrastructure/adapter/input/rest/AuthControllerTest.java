package dev.ivanhernandez.authservice.infrastructure.adapter.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ivanhernandez.authservice.application.dto.request.IntrospectRequest;
import dev.ivanhernandez.authservice.application.dto.request.LoginRequest;
import dev.ivanhernandez.authservice.application.dto.request.RegisterRequest;
import dev.ivanhernandez.authservice.application.dto.response.AuthResponse;
import dev.ivanhernandez.authservice.application.dto.response.IntrospectResponse;
import dev.ivanhernandez.authservice.application.dto.response.UserProfileResponse;
import dev.ivanhernandez.authservice.application.port.input.*;
import dev.ivanhernandez.authservice.application.port.output.JwtProvider;
import dev.ivanhernandez.authservice.application.port.output.RateLimiter;
import dev.ivanhernandez.authservice.application.port.output.TokenBlacklist;
import dev.ivanhernandez.authservice.domain.exception.InvalidCredentialsException;
import dev.ivanhernandez.authservice.domain.exception.UserAlreadyExistsException;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AuthController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = RateLimitingFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegisterUserUseCase registerUserUseCase;
    @MockBean
    private LoginUseCase loginUseCase;
    @MockBean
    private RefreshTokenUseCase refreshTokenUseCase;
    @MockBean
    private LogoutUseCase logoutUseCase;
    @MockBean
    private VerifyEmailUseCase verifyEmailUseCase;
    @MockBean
    private RequestPasswordResetUseCase requestPasswordResetUseCase;
    @MockBean
    private ResetPasswordUseCase resetPasswordUseCase;
    @MockBean
    private IntrospectTokenUseCase introspectTokenUseCase;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private RateLimiter rateLimiter;
    @MockBean
    private TokenBlacklist tokenBlacklist;

    @Test
    @DisplayName("POST /auth/register should return 201 for valid request")
    void register_shouldReturn201_forValidRequest() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "acme",
                "john@acme.com",
                "SecurePass1!",
                "John",
                "Doe"
        );

        UserProfileResponse response = new UserProfileResponse(
                UUID.randomUUID(),
                "john@acme.com",
                "John",
                "Doe",
                "acme",
                "ACME",
                List.of("USER"),
                false,
                LocalDateTime.now()
        );

        when(registerUserUseCase.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john@acme.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @DisplayName("POST /auth/register should return 400 for invalid email")
    void register_shouldReturn400_forInvalidEmail() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "acme",
                "invalid-email",
                "SecurePass1!",
                "John",
                "Doe"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    @DisplayName("POST /auth/register should return 400 for weak password")
    void register_shouldReturn400_forWeakPassword() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "acme",
                "john@acme.com",
                "weak",
                "John",
                "Doe"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    @DisplayName("POST /auth/register should return 409 for existing user")
    void register_shouldReturn409_forExistingUser() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "acme",
                "john@acme.com",
                "SecurePass1!",
                "John",
                "Doe"
        );

        when(registerUserUseCase.register(any()))
                .thenThrow(new UserAlreadyExistsException("john@acme.com"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /auth/login should return 200 for valid credentials")
    void login_shouldReturn200_forValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("acme", "john@acme.com", "SecurePass1!");

        UserProfileResponse userResponse = new UserProfileResponse(
                UUID.randomUUID(),
                "john@acme.com",
                "John",
                "Doe",
                "acme",
                "ACME",
                List.of("USER"),
                true,
                LocalDateTime.now()
        );

        AuthResponse response = AuthResponse.of(
                "accessToken",
                "refreshToken",
                900000L,
                userResponse
        );

        when(loginUseCase.login(any(), any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("POST /auth/login should return 401 for invalid credentials")
    void login_shouldReturn401_forInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("acme", "john@acme.com", "wrongpassword");

        when(loginUseCase.login(any(), any(), any()))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /auth/introspect should return active response for valid token")
    void introspect_shouldReturnActiveResponse_forValidToken() throws Exception {
        IntrospectRequest request = new IntrospectRequest("valid.token.here");
        UUID userId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        IntrospectResponse response = IntrospectResponse.active(
                userId,
                tenantId,
                "acme",
                "user@acme.com",
                List.of("USER")
        );

        when(introspectTokenUseCase.introspect(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/introspect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.tenantId").value(tenantId.toString()))
                .andExpect(jsonPath("$.tenantSlug").value("acme"))
                .andExpect(jsonPath("$.email").value("user@acme.com"));
    }

    @Test
    @DisplayName("POST /auth/introspect should return inactive response for invalid token")
    void introspect_shouldReturnInactiveResponse_forInvalidToken() throws Exception {
        IntrospectRequest request = new IntrospectRequest("invalid.token");

        when(introspectTokenUseCase.introspect(any())).thenReturn(IntrospectResponse.inactive());

        mockMvc.perform(post("/api/v1/auth/introspect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.userId").doesNotExist());
    }
}
