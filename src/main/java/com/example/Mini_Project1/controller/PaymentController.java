package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.request.payment.CreatePaymentRequest;
import com.example.Mini_Project1.response.payment.PaymentResponse;
import com.example.Mini_Project1.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
  public ResponseEntity<PaymentResponse> checkoutCart(
      @RequestParam String userId, @RequestParam(required = false) String voucherCode) {
    PaymentResponse paymentResponse = paymentService.checkoutCart(userId, voucherCode);
    return ResponseEntity.ok(paymentResponse);
  }
}
