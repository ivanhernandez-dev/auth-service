package dev.ivanhernandez.authservice.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth Service API")
                        .description("Multi-tenant authentication and authorization microservice for SaaS applications")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Ivan Hernandez")
                                .url("https://ivanhernandez.dev"))
                        .license(new License()
                                .name("CC BY-NC 4.0")
                                .url("https://creativecommons.org/licenses/by-nc/4.0/")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token"))
                        .addSchemas("ErrorResponse", createErrorResponseSchema())
                        .addSchemas("ValidationErrorResponse", createValidationErrorResponseSchema()));
    }

    private Schema<?> createErrorResponseSchema() {
        return new Schema<>()
                .type("object")
                .description("Standard error response")
                .addProperty("status", new Schema<>().type("integer").description("HTTP status code").example(404))
                .addProperty("message", new Schema<>().type("string").description("Error message").example("Resource not found"))
                .addProperty("timestamp", new Schema<>().type("string").format("date-time").description("Error timestamp"));
    }

    private Schema<?> createValidationErrorResponseSchema() {
        return new Schema<>()
                .type("object")
                .description("Validation error response with field-level errors")
                .addProperty("status", new Schema<>().type("integer").description("HTTP status code").example(400))
                .addProperty("message", new Schema<>().type("string").description("Error message").example("Validation failed"))
                .addProperty("errors", new MapSchema()
                        .additionalProperties(new StringSchema())
                        .description("Map of field names to error messages")
                        .example(Map.of("email", "must be a valid email address", "password", "must be at least 8 characters")))
                .addProperty("timestamp", new Schema<>().type("string").format("date-time").description("Error timestamp"));
    }
}
