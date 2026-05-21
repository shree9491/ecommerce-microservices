package com.ecommerce.payment_service.kafka;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentEvent {
    private UUID paymentId;
    private UUID orderId;
    private UUID userId;
    private BigDecimal amount;
    private String status;
    private String eventType;
}