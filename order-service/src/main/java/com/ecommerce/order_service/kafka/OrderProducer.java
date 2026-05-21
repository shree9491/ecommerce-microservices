package com.ecommerce.order_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void sendOrderEvent(OrderEvent event) {
        kafkaTemplate.send("order.created", event);
        log.info("Order event sent: {} for order: {}",
                event.getEventType(), event.getOrderId());
    }
}