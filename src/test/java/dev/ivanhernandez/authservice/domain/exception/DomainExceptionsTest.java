package dev.ivanhernandez.authservice.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomainExceptionsTest {

    @Test
    @DisplayName("UserNotFoundException should contain email in message")
    void userNotFoundException_shouldContainEmailInMessage() {
        UserNotFoundException ex = new UserNotFoundException("john@acme.com");

        assertTrue(ex.getMessage().contains("john@acme.com"));
    }

    @Test
    @DisplayName("TenantNotFoundException should contain slug in message")
    void tenantNotFoundException_shouldContainSlugInMessage() {
        TenantNotFoundException ex = new TenantNotFoundException("acme");

        assertTrue(ex.getMessage().contains("acme"));
    }

    @Test
    @DisplayName("InvalidCredentialsException should have generic message")
    void invalidCredentialsException_shouldHaveGenericMessage() {
        InvalidCredentialsException ex = new InvalidCredentialsException();

        assertEquals("Invalid email or password", ex.getMessage());
    }

    @Test
    @DisplayName("UserAlreadyExistsException should contain email in message")
    void userAlreadyExistsException_shouldContainEmailInMessage() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("john@acme.com");

        assertTrue(ex.getMessage().contains("john@acme.com"));
    }

    @Test
    @DisplayName("RateLimitExceededException should contain retry time")
    void rateLimitExceededException_shouldContainRetryTime() {
        RateLimitExceededException ex = new RateLimitExceededException(300);

        assertEquals(300, ex.getRetryAfterSeconds());
        assertTrue(ex.getMessage().contains("300"));
    }

    @Test
    @DisplayName("TokenExpiredException should have correct message")
    void tokenExpiredException_shouldHaveCorrectMessage() {
        TokenExpiredException ex = new TokenExpiredException();

        assertEquals("Token has expired", ex.getMessage());
    }

    @Test
    @DisplayName("TokenRevokedException should have correct message")
    void tokenRevokedException_shouldHaveCorrectMessage() {
        TokenRevokedException ex = new TokenRevokedException();

        assertEquals("Token has been revoked", ex.getMessage());
    }

    @Test
    @DisplayName("InvalidTokenException should have default message")
    void invalidTokenException_shouldHaveDefaultMessage() {
        InvalidTokenException ex = new InvalidTokenException();

        assertEquals("Invalid token", ex.getMessage());
    }

    @Test
    @DisplayName("InvalidTokenException should accept custom message")
    void invalidTokenException_shouldAcceptCustomMessage() {
        InvalidTokenException ex = new InvalidTokenException("Custom message");

        assertEquals("Custom message", ex.getMessage());
    }

    @Test
    @DisplayName("UserNotVerifiedException should contain email in message")
    void userNotVerifiedException_shouldContainEmailInMessage() {
        UserNotVerifiedException ex = new UserNotVerifiedException("john@acme.com");

        assertTrue(ex.getMessage().contains("john@acme.com"));
    }

    @Test
    @DisplayName("TenantDisabledException should contain slug in message")
    void tenantDisabledException_shouldContainSlugInMessage() {
        TenantDisabledException ex = new TenantDisabledException("acme");

        assertTrue(ex.getMessage().contains("acme"));
    }

    @Test
    @DisplayName("UserDisabledException should contain email in message")
    void userDisabledException_shouldContainEmailInMessage() {
        UserDisabledException ex = new UserDisabledException("john@acme.com");

        assertTrue(ex.getMessage().contains("john@acme.com"));
    }
}
