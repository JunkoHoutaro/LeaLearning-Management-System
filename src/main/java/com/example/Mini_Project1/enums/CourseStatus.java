package com.example.Mini_Project1.enums;

import lombok.Getter;

@Getter
public enum CourseStatus {
    PENDING(1), ACCEPTED(2), DELETED(3);

    final int value;

    CourseStatus(int value) {
        this.value = value;
    }
}
