package dev.ivanhernandez.authservice.application.port.input;

public interface VerifyEmailUseCase {

    void verify(String token);
}
