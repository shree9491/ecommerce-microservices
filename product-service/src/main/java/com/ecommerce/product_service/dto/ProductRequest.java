package com.ecommerce.product_service.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private UUID categoryId;
    private String sku;
}