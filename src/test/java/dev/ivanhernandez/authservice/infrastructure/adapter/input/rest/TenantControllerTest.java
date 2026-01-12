package dev.ivanhernandez.authservice.infrastructure.adapter.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ivanhernandez.authservice.application.dto.request.CreateTenantRequest;
import dev.ivanhernandez.authservice.application.dto.response.TenantResponse;
import dev.ivanhernandez.authservice.application.port.input.CreateTenantUseCase;
import dev.ivanhernandez.authservice.application.port.input.GetTenantUseCase;
import dev.ivanhernandez.authservice.application.port.output.JwtProvider;
import dev.ivanhernandez.authservice.application.port.output.RateLimiter;
import dev.ivanhernandez.authservice.application.port.output.TokenBlacklist;
import dev.ivanhernandez.authservice.domain.exception.TenantNotFoundException;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = TenantController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = RateLimitingFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class TenantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateTenantUseCase createTenantUseCase;
    @MockBean
    private GetTenantUseCase getTenantUseCase;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private RateLimiter rateLimiter;
    @MockBean
    private TokenBlacklist tokenBlacklist;

    @Test
    @DisplayName("POST /tenants should create tenant")
    void create_shouldCreateTenant() throws Exception {
        CreateTenantRequest request = new CreateTenantRequest("ACME Corporation", "acme");
        TenantResponse response = new TenantResponse(
                UUID.randomUUID(),
                "ACME Corporation",
                "acme",
                true,
                LocalDateTime.now()
        );

        when(createTenantUseCase.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("ACME Corporation"))
                .andExpect(jsonPath("$.slug").value("acme"));
    }

    @Test
    @DisplayName("POST /tenants should return 400 for invalid slug")
    void create_shouldReturn400_forInvalidSlug() throws Exception {
        CreateTenantRequest request = new CreateTenantRequest("ACME", "INVALID SLUG!");

        mockMvc.perform(post("/api/v1/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.slug").exists());
    }

    @Test
    @DisplayName("GET /tenants/{slug} should return tenant")
    void getBySlug_shouldReturnTenant() throws Exception {
        TenantResponse response = new TenantResponse(
                UUID.randomUUID(),
                "ACME Corporation",
                "acme",
                true,
                LocalDateTime.now()
        );

        when(getTenantUseCase.getBySlug("acme")).thenReturn(response);

        mockMvc.perform(get("/api/v1/tenants/acme"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ACME Corporation"))
                .andExpect(jsonPath("$.slug").value("acme"));
    }

    @Test
    @DisplayName("GET /tenants/{slug} should return 404 when not found")
    void getBySlug_shouldReturn404_whenNotFound() throws Exception {
        when(getTenantUseCase.getBySlug("unknown")).thenThrow(new TenantNotFoundException("unknown"));

        mockMvc.perform(get("/api/v1/tenants/unknown"))
                .andExpect(status().isNotFound());
    }
}
