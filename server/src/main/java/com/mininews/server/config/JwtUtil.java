package com.mininews.server.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mininews.server.common.AuthUser;
import com.mininews.server.common.Role;
import com.mininews.server.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final ObjectMapper objectMapper;

    @Value("${mininews.jwt.secret}")
    private String secret;

    @Value("${mininews.jwt.ttl-seconds:86400}")
    private long ttlSeconds;

    public JwtUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }

    public String generateToken(User user) {
        try {
            String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

            long now = Instant.now().getEpochSecond();
            long exp = now + ttlSeconds;

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("uid", user.getId());
            payload.put("username", user.getUsername());
            payload.put("role", user.getRole().name());
            payload.put("exp", exp);

            String payloadJson = objectMapper.writeValueAsString(payload);

            String header = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
            String body = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));

            String signingInput = header + "." + body;
            String signature = base64UrlEncode(hmacSha256(signingInput, secret));

            return signingInput + "." + signature;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate token: " + e.getMessage());
        }
    }

    public AuthUser parseToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            String header = parts[0];
            String body = parts[1];
            String sig = parts[2];

            String signingInput = header + "." + body;
            String expectedSig = base64UrlEncode(hmacSha256(signingInput, secret));

            if (!MessageDigest.isEqual(expectedSig.getBytes(StandardCharsets.UTF_8), sig.getBytes(StandardCharsets.UTF_8))) {
                return null;
            }

            String payloadJson = new String(base64UrlDecode(body), StandardCharsets.UTF_8);
            Map<String, Object> payload = objectMapper.readValue(payloadJson, new TypeReference<Map<String, Object>>() {});

            long exp = toLong(payload.get("exp"));
            long now = Instant.now().getEpochSecond();
            if (now >= exp) {
                return null;
            }

            Long uid = toLong(payload.get("uid"));
            String username = String.valueOf(payload.get("username"));
            Role role = Role.valueOf(String.valueOf(payload.get("role")));

            return new AuthUser(uid, username, role);
        } catch (Exception e) {
            return null;
        }
    }

    private static byte[] hmacSha256(String data, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static byte[] base64UrlDecode(String value) {
        return Base64.getUrlDecoder().decode(value);
    }

    private static long toLong(Object v) {
        if (v == null) return 0L;
        if (v instanceof Number n) return n.longValue();
        return Long.parseLong(String.valueOf(v));
    }
}
