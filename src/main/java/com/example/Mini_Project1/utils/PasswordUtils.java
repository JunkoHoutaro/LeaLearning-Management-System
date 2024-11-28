package com.example.Mini_Project1.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

public class PasswordUtils {
    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public static int checkPassword(String passwordWithOutHash, String hashedPassword) {
        if (passwordEncoder.matches(passwordWithOutHash, hashedPassword) == true) {
            return 1;
        }
        return 0;
    }
}
