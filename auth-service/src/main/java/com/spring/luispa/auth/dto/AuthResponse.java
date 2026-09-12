package com.spring.luispa.auth.dto;

public record AuthResponse(
        String token,
        String tokenType,
        Long expiresIn,
        String userId,
        String email
) {}
