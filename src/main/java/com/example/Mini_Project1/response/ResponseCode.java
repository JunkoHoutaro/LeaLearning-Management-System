package com.example.Mini_Project1.response;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ResponseCode {
    SignUpSuccessfully(1000, "Sign up successfully!"),
    LoginSuccessfully(1000, "Login successfully!");

    private int code;
    private String message;

    public Map<String, Object> responseCodeToJson() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("message", message);
        return map;
    }
}
