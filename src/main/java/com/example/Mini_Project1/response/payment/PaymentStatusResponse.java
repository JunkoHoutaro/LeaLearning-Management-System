package com.example.Mini_Project1.response.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentStatusResponse {

    private String status;
    private int amount;
}
