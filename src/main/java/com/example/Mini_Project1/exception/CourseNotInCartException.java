package com.example.Mini_Project1.exception;

public class CourseNotInCartException extends RuntimeException {

    public CourseNotInCartException(String message) {
        super(message);
    }
}
