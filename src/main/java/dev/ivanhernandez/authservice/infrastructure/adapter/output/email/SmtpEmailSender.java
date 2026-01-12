package dev.ivanhernandez.authservice.infrastructure.adapter.output.email;

import dev.ivanhernandez.authservice.application.port.output.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SmtpEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailSender.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final String baseUrl;

    public SmtpEmailSender(JavaMailSender mailSender,
                           @Value("${app.email.from:noreply@authservice.ivanhernandez.dev}") String fromEmail,
                           @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.baseUrl = baseUrl;
    }

    @Override
    @Async
    public void sendVerificationEmail(String to, String userName, String verificationToken) {
        String subject = "Verify your email address";
        String verificationLink = baseUrl + "/api/v1/auth/verify-email?token=" + verificationToken;
        String content = buildVerificationEmailContent(userName, verificationLink);
        sendEmail(to, subject, content);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String userName, String resetToken) {
        String subject = "Reset your password";
        String resetLink = baseUrl + "/reset-password?token=" + resetToken;
        String content = buildPasswordResetEmailContent(userName, resetLink);
        sendEmail(to, subject, content);
    }

    @Override
    @Async
    public void sendWelcomeEmail(String to, String userName) {
        String subject = "Welcome to Auth Service";
        String content = buildWelcomeEmailContent(userName);
        sendEmail(to, subject, content);
    }

    @Override
    @Async
    public void sendSecurityAlertEmail(String to, String userName, String ipAddress, String userAgent) {
        String subject = "Security Alert: New login detected";
        String content = buildSecurityAlertEmailContent(userName, ipAddress, userAgent);
        sendEmail(to, subject, content);
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }

    private String buildVerificationEmailContent(String userName, String verificationLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <h1 style="color: #2563eb;">Verify Your Email</h1>
                        <p>Hello %s,</p>
                        <p>Thank you for registering. Please verify your email address by clicking the button below:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Verify Email</a>
                        </div>
                        <p>Or copy and paste this link into your browser:</p>
                        <p style="word-break: break-all; color: #666;">%s</p>
                        <p>This link will expire in 24 hours.</p>
                        <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;">
                        <p style="color: #666; font-size: 12px;">If you didn't create an account, you can safely ignore this email.</p>
                    </div>
                </body>
                </html>
                """.formatted(userName, verificationLink, verificationLink);
    }

    private String buildPasswordResetEmailContent(String userName, String resetLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <h1 style="color: #2563eb;">Reset Your Password</h1>
                        <p>Hello %s,</p>
                        <p>We received a request to reset your password. Click the button below to create a new password:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; display: inline-block;">Reset Password</a>
                        </div>
                        <p>Or copy and paste this link into your browser:</p>
                        <p style="word-break: break-all; color: #666;">%s</p>
                        <p>This link will expire in 1 hour.</p>
                        <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;">
                        <p style="color: #666; font-size: 12px;">If you didn't request a password reset, you can safely ignore this email.</p>
                    </div>
                </body>
                </html>
                """.formatted(userName, resetLink, resetLink);
    }

    private String buildWelcomeEmailContent(String userName) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <h1 style="color: #2563eb;">Welcome!</h1>
                        <p>Hello %s,</p>
                        <p>Your email has been verified successfully. Welcome to Auth Service!</p>
                        <p>You can now sign in and start using all features.</p>
                        <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;">
                        <p style="color: #666; font-size: 12px;">Thank you for joining us.</p>
                    </div>
                </body>
                </html>
                """.formatted(userName);
    }

    private String buildSecurityAlertEmailContent(String userName, String ipAddress, String userAgent) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <h1 style="color: #dc2626;">Security Alert</h1>
                        <p>Hello %s,</p>
                        <p>We detected a new sign-in to your account:</p>
                        <ul>
                            <li><strong>IP Address:</strong> %s</li>
                            <li><strong>Device:</strong> %s</li>
                        </ul>
                        <p>If this was you, you can ignore this email.</p>
                        <p>If you don't recognize this activity, please secure your account immediately by changing your password.</p>
                        <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;">
                        <p style="color: #666; font-size: 12px;">This is an automated security notification.</p>
                    </div>
                </body>
                </html>
                """.formatted(userName, ipAddress, userAgent);
    }
}
