package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.request.token.TokenRequest;
import com.example.Mini_Project1.request.user.CreateUserRequest;
import com.example.Mini_Project1.request.user.LoginRequest;
import com.example.Mini_Project1.request.user.UpdateUserRequest;
import com.example.Mini_Project1.response.user.TokenResponse;
import com.example.Mini_Project1.response.user.UserResponse;
import com.example.Mini_Project1.service.UserService;
import com.example.Mini_Project1.utils.JwtTokenUtils;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import lombok.AllArgsConstructor;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody CreateUserRequest createUserRequest) {
        return ResponseEntity.ok(userService.createUser(createUserRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody TokenRequest tokenRequest) {
        return ResponseEntity.ok(userService.refreshToken(tokenRequest.getToken()));
    }

    @PostMapping("/revoke-token")
    public ResponseEntity<Void> revokeToken(@RequestBody TokenRequest tokenRequest) {
        userService.revokeToken(tokenRequest.getToken());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/google-login")
    public ResponseEntity<Void> googleLogin() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/oauth2/authorization/google"))
                .build();
    }

    @GetMapping("/google-login-success")
    public ResponseEntity<Void> googleLoginSuccess(@AuthenticationPrincipal OAuth2User oauth2User) {
        var tokenResponse = userService.googleLogin(oauth2User);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:3000/login?accessToken=" + tokenResponse.getAccessToken() + "&refreshToken=" + tokenResponse.getRefreshToken()))
                .build();
    }

    @GetMapping("/google-login-failure")
    public ResponseEntity<String> googleLoginFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Google login failed");
    }
}