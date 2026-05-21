package com.ecommerce.payment_service.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentResponse {
    private UUID id;
    private UUID orderId;
    private UUID userId;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime createdAt;
}