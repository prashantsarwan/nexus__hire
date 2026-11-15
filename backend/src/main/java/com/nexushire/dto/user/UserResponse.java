package com.nexushire.dto.user;

import com.nexushire.entity.Role;
import com.nexushire.entity.User;

public record UserResponse(
        Long id,
        String email,
        String fullName,
        Role role,
        String phone,
        String profilePictureUrl,
        boolean verified
) {
    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(),
                u.getEmail(),
                u.getFullName(),
                u.getRole(),
                u.getPhone(),
                u.getProfilePictureUrl(),
                u.isVerified()
        );
    }
}
