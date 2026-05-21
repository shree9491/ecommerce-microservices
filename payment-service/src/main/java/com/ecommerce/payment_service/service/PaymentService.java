package com.ecommerce.payment_service.service;

import com.ecommerce.payment_service.dto.*;
import com.ecommerce.payment_service.kafka.*;
import com.ecommerce.payment_service.model.Payment;
import com.ecommerce.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;

    @Transactional
    public void processPayment(OrderEvent event) {
        // Simulate payment: always succeeds (mock)
        String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .amount(event.getTotalAmount())
                .status("SUCCESS")
                .transactionId(txnId)
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment processed: {} for order: {}", txnId, event.getOrderId());

        paymentProducer.sendPaymentEvent(PaymentEvent.builder()
                .paymentId(saved.getId())
                .orderId(saved.getOrderId())
                .userId(saved.getUserId())
                .amount(saved.getAmount())
                .status("SUCCESS")
                .eventType("PAYMENT_SUCCESS")
                .build());
    }

    @Transactional
    public PaymentResponse manualPay(PaymentRequest request) {
        String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .status("SUCCESS")
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "MOCK_CARD")
                .transactionId(txnId)
                .build();

        return toResponse(paymentRepository.save(payment));
    }

    public PaymentResponse getByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }

    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .orderId(p.getOrderId())
                .userId(p.getUserId())
                .amount(p.getAmount())
                .status(p.getStatus())
                .paymentMethod(p.getPaymentMethod())
                .transactionId(p.getTransactionId())
                .createdAt(p.getCreatedAt())
                .build();
    }
}