package dev.ivanhernandez.authservice.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitConfig {

    private Map<String, EndpointLimit> endpoints = new HashMap<>();

    public RateLimitConfig() {
        endpoints.put("/api/v1/auth/login", new EndpointLimit(5, Duration.ofMinutes(15)));
        endpoints.put("/api/v1/auth/register", new EndpointLimit(3, Duration.ofHours(1)));
        endpoints.put("/api/v1/auth/password/reset-request", new EndpointLimit(3, Duration.ofHours(1)));
        endpoints.put("/api/v1/auth/password/reset", new EndpointLimit(5, Duration.ofHours(1)));
    }

    public Map<String, EndpointLimit> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Map<String, EndpointLimit> endpoints) {
        this.endpoints = endpoints;
    }

    public EndpointLimit getLimit(String path) {
        return endpoints.get(path);
    }

    public static class EndpointLimit {
        private int maxAttempts;
        private Duration window;

        public EndpointLimit() {
        }

        public EndpointLimit(int maxAttempts, Duration window) {
            this.maxAttempts = maxAttempts;
            this.window = window;
        }

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public Duration getWindow() {
            return window;
        }

        public void setWindow(Duration window) {
            this.window = window;
        }
    }
}
