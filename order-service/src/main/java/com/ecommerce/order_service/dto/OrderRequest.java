package com.ecommerce.order_service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class OrderRequest {
    private UUID userId;
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        private UUID productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}