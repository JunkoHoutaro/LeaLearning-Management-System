package com.example.Mini_Project1.exception;

public class UserAlreadyExistsException extends RuntimeException {

  public UserAlreadyExistsException(String message) {
    super(message);
  }
}
