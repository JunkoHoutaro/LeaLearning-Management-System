package com.example.Mini_Project1.request.payment;

import lombok.Data;

@Data
public class CreatePaymentRequest {
  private String userId;
  private String courseId;
  private String voucherCode;
}