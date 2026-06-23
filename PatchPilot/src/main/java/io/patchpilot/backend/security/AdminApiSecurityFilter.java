package io.patchpilot.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.common.response.ApiResponse;
import io.patchpilot.backend.security.config.AdminApiSecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class AdminApiSecurityFilter extends OncePerRequestFilter {

    public static final String ADMIN_TOKEN_HEADER = "X-PatchPilot-Admin-Token";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ADMIN_TOKEN_REQUIRED_MESSAGE = "Admin token is required";

    private final AdminApiSecurityProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!properties.isAdminTokenConfigured()) {
            return true;
        }
        String method = request.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }
        String path = request.getRequestURI();
        return path.equals("/health")
                || path.startsWith("/actuator")
                || path.equals("/api/github/webhook");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (isAuthorized(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), ApiResponse.fail(ADMIN_TOKEN_REQUIRED_MESSAGE));
    }

    private boolean isAuthorized(HttpServletRequest request) {
        String token = request.getHeader(ADMIN_TOKEN_HEADER);
        if (!StringUtils.hasText(token)) {
            token = bearerToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        }
        return properties.getAdminToken().equals(token);
    }

    private static String bearerToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return "";
        }
        return authorizationHeader.substring(BEARER_PREFIX.length()).trim();
    }
}
