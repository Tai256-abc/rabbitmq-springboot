package com.ecommerce.controller;

import com.ecommerce.dto.PaymentRequest;
import com.ecommerce.dto.PaymentResponse;
import com.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/stripe")
    public ResponseEntity<PaymentResponse> processStripePayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processStripePayment(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/paypal")
    public ResponseEntity<PaymentResponse> processPayPalPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayPalPayment(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/razorpay")
    public ResponseEntity<PaymentResponse> processRazorpayPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processRazorpayPayment(request);
        return ResponseEntity.ok(response);
    }
}
