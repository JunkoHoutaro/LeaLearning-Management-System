package com.example.Mini_Project1.exception;

public class NegativePriceException extends RuntimeException {

    public NegativePriceException(String message) {
        super(message);
    }
}
