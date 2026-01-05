package com.mininews.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mininews.server.common.ApiResponse;
import com.mininews.server.common.AuthUser;
import com.mininews.server.common.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String ATTR_AUTH_USER = "AUTH_USER";

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public AuthInterceptor(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String path = request.getRequestURI();
        String method = request.getMethod();

        boolean adminRequired = path.startsWith("/api/admin/");
        boolean commentPostRequiresLogin = ("POST".equalsIgnoreCase(method) && path.matches("^/api/news/\\d+/comments$"));

        if (!adminRequired && !commentPostRequiresLogin) {
            return true;
        }

        String token = extractBearerToken(request);
        if (token == null || token.isBlank()) {
            writeJson(response, 401, ApiResponse.fail(401, "Unauthorized: missing token"));
            return false;
        }

        AuthUser user = jwtUtil.parseToken(token);
        if (user == null) {
            writeJson(response, 401, ApiResponse.fail(401, "Unauthorized: invalid or expired token"));
            return false;
        }

        request.setAttribute(ATTR_AUTH_USER, user);

        if (adminRequired && user.role() != Role.ADMIN) {
            writeJson(response, 403, ApiResponse.fail(403, "Forbidden: ADMIN required"));
            return false;
        }

        return true;
    }

    private String extractBearerToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null) return null;
        if (auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return null;
    }

    private void writeJson(HttpServletResponse response, int httpStatus, ApiResponse<?> body) throws Exception {
        response.setStatus(httpStatus);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
