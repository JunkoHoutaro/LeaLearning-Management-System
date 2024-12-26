package com.example.Mini_Project1.exception;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

  private int status;
  private String error;
  private String message;
  private String timestamp;

  public ErrorResponse(int status, String error, String message) {
    this.status = status;
    this.error = error;
    this.message = message;
    this.timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
  }
}
