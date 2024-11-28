package com.example.Mini_Project1.exception;

import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ExceptionCode {
    LocalDateFormat(1001, "The localdate format is wrong", HttpStatus.BAD_REQUEST),
    EmailAlreadyExists(1001, "This email already exists in the database", HttpStatus.BAD_REQUEST),
    EmailDoesNotExist(1001, "This email does not exist in the database", HttpStatus.BAD_REQUEST),
    WrongPassword(1001, "Your password is wrong", HttpStatus.BAD_REQUEST),
    CreateTokenFail(1001, "Create token fail", HttpStatus.BAD_REQUEST),
    VerifyTokenFail(1001, "Verify token fail", HttpStatus.BAD_REQUEST);

    private int code;
    private String message;
    private HttpStatus status;

    public Map<String, Object> exceptionCodeToJson() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("message", message);
        return map;
    }
}
