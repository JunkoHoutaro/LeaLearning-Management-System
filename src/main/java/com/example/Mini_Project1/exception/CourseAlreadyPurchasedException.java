package com.example.Mini_Project1.exception;

public class CourseAlreadyPurchasedException extends RuntimeException {

    public CourseAlreadyPurchasedException(String message) {
        super(message);
    }
}
