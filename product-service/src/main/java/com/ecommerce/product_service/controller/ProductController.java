package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.dto.*;
import com.ecommerce.product_service.model.Product;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable UUID id,
            @RequestParam int quantity) {
        return ResponseEntity.ok(productService.updateStock(id, quantity));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Product Service is UP!");
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<Map<String, Object>> getProductStats() {
        List<Product> all = productRepository.findAll();

        long totalProducts = all.size();
        long activeProducts = all.stream()
                .filter(p -> Boolean.TRUE.equals(p.getActive())).count();
        long outOfStock = all.stream()
                .filter(p -> p.getStockQuantity() == 0).count();
        long lowStock = all.stream()
                .filter(p -> p.getStockQuantity() > 0
                        && p.getStockQuantity() <= 10).count();

        return ResponseEntity.ok(Map.of(
                "totalProducts", totalProducts,
                "activeProducts", activeProducts,
                "outOfStock", outOfStock,
                "lowStock", lowStock,
                "products", all.stream()
                        .map(p -> Map.of(
                                "id", p.getId().toString(),
                                "name", p.getName(),
                                "price", p.getPrice(),
                                "stockQuantity", p.getStockQuantity(),
                                "active", p.getActive()
                        ))
                        .collect(Collectors.toList())
        ));
    }
}