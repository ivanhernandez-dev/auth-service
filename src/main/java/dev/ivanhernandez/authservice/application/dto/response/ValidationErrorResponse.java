package dev.ivanhernandez.authservice.application.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(
        int status,
        String message,
        Map<String, String> errors,
        LocalDateTime timestamp
) {
    public static ValidationErrorResponse of(int status, String message, Map<String, String> errors) {
        return new ValidationErrorResponse(status, message, errors, LocalDateTime.now());
    }
}
