package com.example.Mini_Project1.exception;

public class VoucherAlreadyUsedException extends RuntimeException {

    public VoucherAlreadyUsedException(String message) {
        super(message);
    }
}
