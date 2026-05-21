package com.ecommerce.payment_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public void sendPaymentEvent(PaymentEvent event) {
        kafkaTemplate.send("payment.processed", event);
        log.info("Payment event sent: {} for order: {}", event.getEventType(), event.getOrderId());
    }
}