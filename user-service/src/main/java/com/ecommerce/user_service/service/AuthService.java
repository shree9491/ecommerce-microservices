package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dto.*;
import com.ecommerce.user_service.model.*;
import com.ecommerce.user_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshExpiry;

    @Transactional
    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username already taken!");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already registered!");

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role("CUSTOMER")
                .build();

        userRepository.save(user);
        return "User registered successfully!";
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
            throw new RuntimeException("Invalid password!");

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = generateAndSaveRefreshToken(user);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(900)
                .username(user.getUsername())
                .role(user.getRole())
                .userId(user.getId().toString())
                .build();
    }

    @Transactional
    public TokenResponse refresh(String refreshToken) {
        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token!"));
        if (stored.getRevoked())
            throw new RuntimeException("Token revoked!");
        if (stored.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Token expired!");

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        String newAccess = tokenService.generateAccessToken(stored.getUser());
        String newRefresh = generateAndSaveRefreshToken(stored.getUser());

        return TokenResponse.builder()
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .tokenType("Bearer")
                .expiresIn(900)
                .username(stored.getUser().getUsername())
                .role(stored.getUser().getRole())
                .userId(stored.getUser().getId().toString())
                .build();
    }

    private String generateAndSaveRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpiry / 1000))
                .revoked(false)
                .build());
        return token;
    }
}