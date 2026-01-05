package com.mininews.server.common;

public record AuthUser(Long id, String username, Role role) {
}
