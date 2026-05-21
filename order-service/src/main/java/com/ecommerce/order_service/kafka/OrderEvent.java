package com.ecommerce.order_service.kafka;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private UUID orderId;
    private UUID userId;
    private BigDecimal totalAmount;
    private String status;
    private String eventType;
}