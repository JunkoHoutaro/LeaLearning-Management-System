package com.example.Mini_Project1.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Mini_Project1.request.payment.CreatePaymentRequest;
import com.example.Mini_Project1.response.payment.PaymentResponse;
import com.example.Mini_Project1.response.payment.PaymentStatusResponse;
import com.example.Mini_Project1.service.PaymentService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody CreatePaymentRequest request) {
        PaymentResponse paymentResponse = paymentService.createPayment(request);
        return ResponseEntity.ok(paymentResponse);
    }

    @PostMapping("/checkout")
    public ResponseEntity<List<PaymentResponse>> checkout(@RequestParam String userId, @RequestParam(required = false) String voucherCode) {
        List<PaymentResponse> paymentResponse = paymentService.checkoutCart(userId, voucherCode);
        return ResponseEntity.ok(paymentResponse);
    }

    @GetMapping("/status")
    public ResponseEntity<PaymentStatusResponse> checkPaymentStatus(@RequestParam String userId, @RequestParam String courseId) {
        PaymentStatusResponse paymentStatusResponse = paymentService.checkPaymentStatus(userId, courseId);
        return ResponseEntity.ok(paymentStatusResponse);
    }
}
