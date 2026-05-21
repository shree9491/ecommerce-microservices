package com.ecommerce.notification_service.kafka;

import com.ecommerce.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "order.created", groupId = "notification-group",
            properties = {"spring.json.value.default.type=com.ecommerce.notification_service.kafka.NotificationEvent"})
    public void handleOrderCreated(NotificationEvent event) {
        log.info("Notification consumer received ORDER event: {} for order: {}",
                event.getEventType(), event.getOrderId());
        notificationService.sendOrderNotification(event);
    }
}