package dev.ivanhernandez.authservice.infrastructure.adapter.input.rest;

import dev.ivanhernandez.authservice.application.dto.request.CreateTenantRequest;
import dev.ivanhernandez.authservice.application.dto.response.TenantResponse;
import dev.ivanhernandez.authservice.application.port.input.CreateTenantUseCase;
import dev.ivanhernandez.authservice.application.port.input.GetTenantUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tenants")
@Tag(name = "Tenants", description = "Tenant management endpoints")
public class TenantController {

    private final CreateTenantUseCase createTenantUseCase;
    private final GetTenantUseCase getTenantUseCase;

    public TenantController(CreateTenantUseCase createTenantUseCase,
                            GetTenantUseCase getTenantUseCase) {
        this.createTenantUseCase = createTenantUseCase;
        this.getTenantUseCase = getTenantUseCase;
    }

    @PostMapping
    @Operation(summary = "Create a new tenant", description = "Creates a new tenant organization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tenant created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))),
            @ApiResponse(responseCode = "409", description = "Tenant with this slug already exists", content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<TenantResponse> create(@Valid @RequestBody CreateTenantRequest request) {
        TenantResponse response = createTenantUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get tenant by slug", description = "Returns tenant information by its slug")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tenant retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Tenant not found", content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<TenantResponse> getBySlug(@PathVariable String slug) {
        TenantResponse response = getTenantUseCase.getBySlug(slug);
        return ResponseEntity.ok(response);
    }
}
