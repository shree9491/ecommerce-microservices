package com.ecommerce.payment_service.kafka;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderEvent {
    private UUID orderId;
    private UUID userId;
    private BigDecimal totalAmount;
    private String status;
    private String eventType;
}