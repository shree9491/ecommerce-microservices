package com.ecommerce.notification_service.service;

import com.ecommerce.notification_service.kafka.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void sendOrderNotification(NotificationEvent event) {
        log.info("==================================================");
        log.info("📦 ORDER NOTIFICATION");
        log.info("To: User {}", event.getUserId());
        log.info("Order ID: {}", event.getOrderId());
        log.info("Status: {}", event.getStatus());
        log.info("Amount: ₹{}", event.getAmount());
        log.info("Message: Your order has been placed successfully!");
        log.info("==================================================");
    }

    public void sendPaymentNotification(NotificationEvent event) {
        log.info("==================================================");
        log.info("💳 PAYMENT NOTIFICATION");
        log.info("To: User {}", event.getUserId());
        log.info("Order ID: {}", event.getOrderId());
        log.info("Status: {}", event.getStatus());
        log.info("Amount: ₹{}", event.getAmount());
        if ("SUCCESS".equals(event.getStatus())) {
            log.info("Message: Payment of ₹{} received successfully!", event.getAmount());
        } else {
            log.info("Message: Payment failed. Please try again.");
        }
        log.info("==================================================");
    }
}