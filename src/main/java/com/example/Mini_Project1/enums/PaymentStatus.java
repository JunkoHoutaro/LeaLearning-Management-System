package com.example.Mini_Project1.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING(1), CANCELED(2), SUCCESS(3);

    final int value;

    PaymentStatus(int value) {
        this.value = value;
    }
}
