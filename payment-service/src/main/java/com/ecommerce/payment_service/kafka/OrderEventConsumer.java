package com.ecommerce.payment_service.kafka;

import com.ecommerce.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final PaymentService paymentService;

    @KafkaListener(topics = "order.created", groupId = "payment-group")
    public void handleOrderCreated(OrderEvent event) {
        log.info("Received order event: {} for order: {}", event.getEventType(), event.getOrderId());
        if ("ORDER_CREATED".equals(event.getEventType())) {
            paymentService.processPayment(event);
        }
    }
}