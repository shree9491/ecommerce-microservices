package com.ecommerce.user_service.dto;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private String username;
    private String role;
    private String userId;
}