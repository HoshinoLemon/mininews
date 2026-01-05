package com.mininews.server.controller;

import com.mininews.server.common.ApiResponse;
import com.mininews.server.config.JwtUtil;
import com.mininews.server.dto.LoginRequest;
import com.mininews.server.dto.LoginResponse;
import com.mininews.server.dto.RegisterRequest;
import com.mininews.server.entity.User;
import com.mininews.server.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@Valid @RequestBody RegisterRequest req) {
        User u = userService.register(req.getUsername(), req.getPassword());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", u.getId());
        data.put("username", u.getUsername());
        data.put("role", u.getRole().name());

        return ApiResponse.ok("Registered", data);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        User u = userService.authenticate(req.getUsername(), req.getPassword());
        String token = jwtUtil.generateToken(u);

        LoginResponse data = new LoginResponse(
                token,
                "Bearer",
                jwtUtil.getTtlSeconds(),
                u.getId(),
                u.getUsername(),
                u.getRole().name()
        );

        return ApiResponse.ok(data);
    }
}
