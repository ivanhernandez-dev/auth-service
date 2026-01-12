package dev.ivanhernandez.authservice.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ivanhernandez.authservice.application.dto.response.ErrorResponse;
import dev.ivanhernandez.authservice.application.port.output.RateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimiter rateLimiter;
    private final RateLimitConfig rateLimitConfig;
    private final ObjectMapper objectMapper;

    public RateLimitingFilter(RateLimiter rateLimiter,
                              RateLimitConfig rateLimitConfig,
                              ObjectMapper objectMapper) {
        this.rateLimiter = rateLimiter;
        this.rateLimitConfig = rateLimitConfig;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        RateLimitConfig.EndpointLimit limit = rateLimitConfig.getLimit(path);

        if (limit == null || !"POST".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIpAddress(request);
        String key = path + ":" + clientIp;

        if (!rateLimiter.isAllowed(key, limit.getMaxAttempts(), limit.getWindow())) {
            long retryAfter = rateLimiter.getTimeToReset(key);
            sendRateLimitExceededResponse(response, retryAfter);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendRateLimitExceededResponse(HttpServletResponse response, long retryAfter) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", String.valueOf(retryAfter));

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "Rate limit exceeded. Try again in " + retryAfter + " seconds"
        );

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}
