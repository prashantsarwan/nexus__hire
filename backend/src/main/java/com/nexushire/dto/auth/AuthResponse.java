package com.nexushire.dto.auth;

import com.nexushire.dto.user.UserResponse;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        UserResponse user
) {
}
