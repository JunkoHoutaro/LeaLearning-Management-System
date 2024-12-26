package com.example.Mini_Project1.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
  private int statusCode;
  private String error;
  private String message;
}
