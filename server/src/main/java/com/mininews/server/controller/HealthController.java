package com.mininews.server.controller;

import com.mininews.server.common.ApiResponse;
import com.mininews.server.common.AuthUser;
import com.mininews.server.config.AuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/api/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", "UP");
        data.put("time", OffsetDateTime.now().toString());

        boolean dbOk = false;
        String dbError = null;

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SELECT 1");
            dbOk = true;
        } catch (Exception e) {
            dbError = e.getMessage();
        }

        data.put("db", dbOk);
        if (!dbOk) {
            data.put("dbError", dbError);
        }

        return ApiResponse.ok(data);
    }

    /**
     * 用于测试 /api/admin/** 鉴权是否生效
     */
    @GetMapping("/api/admin/ping")
    public ApiResponse<Map<String, Object>> adminPing(HttpServletRequest request) {
        AuthUser user = (AuthUser) request.getAttribute(AuthInterceptor.ATTR_AUTH_USER);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", "ADMIN_OK");
        if (user != null) {
            data.put("userId", user.id());
            data.put("username", user.username());
            data.put("role", user.role().name());
        }
        return ApiResponse.ok(data);
    }
}
