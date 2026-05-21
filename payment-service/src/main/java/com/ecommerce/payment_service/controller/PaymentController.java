package com.ecommerce.payment_service.controller;

import com.ecommerce.payment_service.dto.*;
import com.ecommerce.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> pay(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.manualPay(request));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getByOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(paymentService.getByOrderId(orderId));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Payment Service is UP!");
    }
}