package com.ecommerce.user_service.dto;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
}