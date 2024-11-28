package com.example.Mini_Project1.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserException extends RuntimeException {
    private ExceptionCode exceptionCode;
}
