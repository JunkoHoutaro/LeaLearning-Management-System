package com.example.Mini_Project1.response.cart;

import lombok.Data;

import java.util.List;

@Data
public class CartResponse {
    private String id;
    private String userId;
    private List<String> courseIds;
    private float totalPrice;
    private int totalAmount;
}
