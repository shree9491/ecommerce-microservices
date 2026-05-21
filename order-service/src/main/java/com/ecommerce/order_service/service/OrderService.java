package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.*;
import com.ecommerce.order_service.kafka.OrderEvent;
import com.ecommerce.order_service.kafka.OrderProducer;
import com.ecommerce.order_service.model.*;
import com.ecommerce.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        BigDecimal total = request.getItems().stream()
                .map(i -> i.getUnitPrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .userId(request.getUserId())
                .totalAmount(total)
                .status("PENDING")
                .build();

        List<OrderItem> items = request.getItems().stream()
                .map(i -> OrderItem.builder()
                        .order(order)
                        .productId(i.getProductId())
                        .productName(i.getProductName())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .totalPrice(i.getUnitPrice()
                                .multiply(BigDecimal.valueOf(i.getQuantity())))
                        .build())
                .collect(Collectors.toList());

        order.setItems(items);
        Order saved = orderRepository.save(order);

        orderProducer.sendOrderEvent(OrderEvent.builder()
                .orderId(saved.getId())
                .userId(saved.getUserId())
                .totalAmount(saved.getTotalAmount())
                .status("PENDING")
                .eventType("ORDER_CREATED")
                .build());

        log.info("Order created: {}", saved.getId());
        return toResponse(saved);
    }

    public OrderResponse getOrderById(UUID id) {
        return orderRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
    }

    public List<OrderResponse> getOrdersByUser(UUID userId) {
        return orderRepository.findByUserId(userId)
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateStatus(UUID id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
        order.setStatus(status);
        return toResponse(orderRepository.save(order));
    }

    private OrderResponse toResponse(Order o) {
        List<OrderResponse.ItemDetail> itemDetails = o.getItems() == null ?
                List.of() :
                o.getItems().stream().map(i ->
                                OrderResponse.ItemDetail.builder()
                                        .productId(i.getProductId())
                                        .productName(i.getProductName())
                                        .quantity(i.getQuantity())
                                        .unitPrice(i.getUnitPrice())
                                        .totalPrice(i.getTotalPrice())
                                        .build())
                        .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(o.getId())
                .userId(o.getUserId())
                .status(o.getStatus())
                .totalAmount(o.getTotalAmount())
                .items(itemDetails)
                .createdAt(o.getCreatedAt())
                .build();
    }
}