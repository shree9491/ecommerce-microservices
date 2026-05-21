package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dto.UserResponse;
import com.ecommerce.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUserById(UUID id) {
        return userRepository.findById(id)
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .email(u.getEmail())
                        .firstName(u.getFirstName())
                        .lastName(u.getLastName())
                        .phone(u.getPhone())
                        .role(u.getRole())
                        .build())
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    public UserResponse getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .email(u.getEmail())
                        .firstName(u.getFirstName())
                        .lastName(u.getLastName())
                        .phone(u.getPhone())
                        .role(u.getRole())
                        .build())
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }
}