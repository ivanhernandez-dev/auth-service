package dev.ivanhernandez.authservice.infrastructure.adapter.input.rest;

import dev.ivanhernandez.authservice.application.dto.request.ChangePasswordRequest;
import dev.ivanhernandez.authservice.application.dto.request.UpdateUserProfileRequest;
import dev.ivanhernandez.authservice.application.dto.response.MessageResponse;
import dev.ivanhernandez.authservice.application.dto.response.UserProfileResponse;
import dev.ivanhernandez.authservice.application.port.input.ChangePasswordUseCase;
import dev.ivanhernandez.authservice.application.port.input.GetUserProfileUseCase;
import dev.ivanhernandez.authservice.application.port.input.UpdateUserProfileUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    public UserController(GetUserProfileUseCase getUserProfileUseCase,
                          UpdateUserProfileUseCase updateUserProfileUseCase,
                          ChangePasswordUseCase changePasswordUseCase) {
        this.getUserProfileUseCase = getUserProfileUseCase;
        this.updateUserProfileUseCase = updateUserProfileUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns the profile of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal UUID userId) {
        UserProfileResponse response = getUserProfileUseCase.getProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile", description = "Updates the profile of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))),
            @ApiResponse(responseCode = "401", description = "Not authenticated", content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody UpdateUserProfileRequest request) {
        UserProfileResponse response = updateUserProfileUseCase.update(userId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/password")
    @Operation(summary = "Change password", description = "Changes the password of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(ref = "#/components/schemas/ValidationErrorResponse"))),
            @ApiResponse(responseCode = "401", description = "Not authenticated or wrong current password", content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse"))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(ref = "#/components/schemas/ErrorResponse")))
    })
    public ResponseEntity<MessageResponse> changePassword(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        changePasswordUseCase.changePassword(userId, request);
        return ResponseEntity.ok(MessageResponse.of("Password changed successfully"));
    }
}
