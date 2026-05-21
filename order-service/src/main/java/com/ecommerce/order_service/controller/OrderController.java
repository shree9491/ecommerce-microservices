package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.*;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Order Service is UP!");
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        List<Order> allOrders = orderRepository.findAll();

        long totalOrders = allOrders.size();
        long pendingOrders = allOrders.stream()
                .filter(o -> "PENDING".equals(o.getStatus())).count();
        long completedOrders = allOrders.stream()
                .filter(o -> "COMPLETED".equals(o.getStatus())).count();
        long failedOrders = allOrders.stream()
                .filter(o -> "FAILED".equals(o.getStatus())).count();

        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> "COMPLETED".equals(o.getStatus()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(Map.of(
                "totalOrders", totalOrders,
                "pendingOrders", pendingOrders,
                "completedOrders", completedOrders,
                "failedOrders", failedOrders,
                "totalRevenue", totalRevenue,
                "recentOrders", allOrders.stream()
                        .sorted((a, b) -> b.getCreatedAt()
                                .compareTo(a.getCreatedAt()))
                        .limit(10)
                        .map(o -> Map.of(
                                "id", o.getId().toString(),
                                "userId", o.getUserId().toString(),
                                "status", o.getStatus(),
                                "totalAmount", o.getTotalAmount(),
                                "createdAt", o.getCreatedAt().toString()
                        ))
                        .collect(Collectors.toList())
        ));
    }
}