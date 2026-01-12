package dev.ivanhernandez.authservice.application.dto.response;

import java.time.LocalDateTime;

public record MessageResponse(
        int status,
        String message,
        LocalDateTime timestamp
) {
    public static MessageResponse of(String message) {
        return new MessageResponse(200, message, LocalDateTime.now());
    }

    public static MessageResponse of(int status, String message) {
        return new MessageResponse(status, message, LocalDateTime.now());
    }
}
