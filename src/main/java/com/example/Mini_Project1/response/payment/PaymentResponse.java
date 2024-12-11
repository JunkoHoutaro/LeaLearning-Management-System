package com.example.Mini_Project1.response.payment;

import java.sql.Date;

import lombok.Data;

@Data
public class PaymentResponse {
  private String id;
  private String userId;
  private String courseId;
  private String voucherId;
  private float price;
  private float discount;
  private String content;
  private String paymentUrl;
  private int status;
  private Date createdDate;
  private Date updatedDate;
}