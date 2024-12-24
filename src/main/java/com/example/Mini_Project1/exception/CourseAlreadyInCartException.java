package com.example.Mini_Project1.exception;

public class CourseAlreadyInCartException extends RuntimeException {

    public CourseAlreadyInCartException(String message) {
        super(message);
    }
}
