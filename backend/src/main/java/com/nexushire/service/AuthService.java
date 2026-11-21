package com.nexushire.service;

import com.nexushire.config.JwtService;
import com.nexushire.dto.auth.AuthResponse;
import com.nexushire.dto.auth.LoginRequest;
import com.nexushire.dto.auth.RefreshTokenRequest;
import com.nexushire.dto.auth.RegisterRequest;
import com.nexushire.dto.user.UserResponse;
import com.nexushire.entity.RecruiterProfile;
import com.nexushire.entity.Role;
import com.nexushire.entity.SeekerProfile;
import com.nexushire.entity.User;
import com.nexushire.exception.ApiException;
import com.nexushire.repository.RecruiterProfileRepository;
import com.nexushire.repository.SeekerProfileRepository;
import com.nexushire.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final SeekerProfileRepository seekerProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request.role() == Role.ADMIN) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot self-register as admin");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        }
        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role(request.role())
                .verified(false)
                .active(true)
                .build();
        user = userRepository.save(user);

        if (user.getRole() == Role.JOB_SEEKER) {
            seekerProfileRepository.save(SeekerProfile.builder().user(user).build());
        } else if (user.getRole() == Role.RECRUITER) {
            recruiterProfileRepository.save(RecruiterProfile.builder().user(user).build());
        }

        emailService.send(user.getEmail(), "Welcome to Nexus Hire",
                "Hi " + user.getFullName() + ", your account is ready.");
        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        return buildAuthResponse(user);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String email;
        try {
            email = jwtService.extractUsername(request.refreshToken());
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        UserDetails details = userDetailsService.loadUserByUsername(user.getEmail());
        String access = jwtService.generateAccessToken(details);
        String refresh = jwtService.generateRefreshToken(details);
        return new AuthResponse(access, refresh, jwtService.getAccessExpiration() / 1000,
                UserResponse.from(user));
    }
}
