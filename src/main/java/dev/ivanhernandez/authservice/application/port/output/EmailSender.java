package dev.ivanhernandez.authservice.application.port.output;

public interface EmailSender {

    void sendVerificationEmail(String to, String userName, String verificationToken);

    void sendPasswordResetEmail(String to, String userName, String resetToken);

    void sendWelcomeEmail(String to, String userName);

    void sendSecurityAlertEmail(String to, String userName, String ipAddress, String userAgent);
}
