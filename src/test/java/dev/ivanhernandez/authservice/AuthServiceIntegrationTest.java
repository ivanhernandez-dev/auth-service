package dev.ivanhernandez.authservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ivanhernandez.authservice.application.dto.request.CreateTenantRequest;
import dev.ivanhernandez.authservice.application.dto.request.LoginRequest;
import dev.ivanhernandez.authservice.application.dto.request.RegisterRequest;
import dev.ivanhernandez.authservice.application.dto.response.AuthResponse;
import dev.ivanhernandez.authservice.application.dto.response.TenantResponse;
import dev.ivanhernandez.authservice.application.dto.response.UserProfileResponse;
import dev.ivanhernandez.authservice.application.port.output.EmailSender;
import dev.ivanhernandez.authservice.application.port.output.EmailVerificationTokenRepository;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.entity.UserJpaEntity;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.persistence.spring.SpringDataUserRepository;
import dev.ivanhernandez.authservice.infrastructure.adapter.output.redis.InMemoryRateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringDataUserRepository userRepository;

    @Autowired
    private EmailVerificationTokenRepository verificationTokenRepository;

    @Autowired
    private InMemoryRateLimiter rateLimiter;

    @MockBean
    private EmailSender emailSender;

    private TenantResponse tenant;

    @BeforeEach
    void setUp() throws Exception {
        rateLimiter.clearAll();

        CreateTenantRequest tenantRequest = new CreateTenantRequest("Integration Test Corp", "integration-test");

        MvcResult result = mockMvc.perform(post("/api/v1/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tenantRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        tenant = objectMapper.readValue(result.getResponse().getContentAsString(), TenantResponse.class);
    }

    @Test
    @DisplayName("Full registration and login flow should work")
    void fullRegistrationAndLoginFlow_shouldWork() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "integration-test",
                "integration@test.com",
                "SecurePass1!",
                "Integration",
                "User"
        );

        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("integration@test.com"))
                .andExpect(jsonPath("$.emailVerified").value(false))
                .andReturn();

        UserProfileResponse registeredUser = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), UserProfileResponse.class);

        UserJpaEntity userEntity = userRepository.findById(registeredUser.id()).orElseThrow();
        userEntity.setEmailVerified(true);
        userRepository.save(userEntity);

        LoginRequest loginRequest = new LoginRequest(
                "integration-test",
                "integration@test.com",
                "SecurePass1!"
        );

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(), AuthResponse.class);

        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + authResponse.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("integration@test.com"))
                .andExpect(jsonPath("$.firstName").value("Integration"));
    }

    @Test
    @DisplayName("Login should fail for unverified email")
    void login_shouldFail_forUnverifiedEmail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "integration-test",
                "unverified@test.com",
                "SecurePass1!",
                "Unverified",
                "User"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest(
                "integration-test",
                "unverified@test.com",
                "SecurePass1!"
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Login should fail for wrong password")
    void login_shouldFail_forWrongPassword() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "integration-test",
                "wrongpass@test.com",
                "SecurePass1!",
                "Wrong",
                "Pass"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest(
                "integration-test",
                "wrongpass@test.com",
                "WrongPassword1!"
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Protected endpoint should require authentication")
    void protectedEndpoint_shouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Refresh token flow should work")
    void refreshTokenFlow_shouldWork() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "integration-test",
                "refresh@test.com",
                "SecurePass1!",
                "Refresh",
                "User"
        );

        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        UserProfileResponse registeredUser = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), UserProfileResponse.class);

        UserJpaEntity userEntity = userRepository.findById(registeredUser.id()).orElseThrow();
        userEntity.setEmailVerified(true);
        userRepository.save(userEntity);

        LoginRequest loginRequest = new LoginRequest(
                "integration-test",
                "refresh@test.com",
                "SecurePass1!"
        );

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(), AuthResponse.class);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"" + authResponse.refreshToken() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("Duplicate registration should fail")
    void duplicateRegistration_shouldFail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "integration-test",
                "duplicate@test.com",
                "SecurePass1!",
                "Duplicate",
                "User"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Get tenant by slug should work")
    void getTenantBySlug_shouldWork() throws Exception {
        mockMvc.perform(get("/api/v1/tenants/integration-test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Test Corp"))
                .andExpect(jsonPath("$.slug").value("integration-test"));
    }

    @Test
    @DisplayName("Get non-existent tenant should return 404")
    void getNonExistentTenant_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/tenants/non-existent"))
                .andExpect(status().isNotFound());
    }
}
