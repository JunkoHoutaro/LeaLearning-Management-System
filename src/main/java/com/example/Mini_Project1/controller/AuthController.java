package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.request.token.TokenRequest;
import com.example.Mini_Project1.request.user.CreateUserRequest;
import com.example.Mini_Project1.request.user.LoginRequest;
import com.example.Mini_Project1.response.user.TokenResponse;
import com.example.Mini_Project1.response.user.UserResponse;
import com.example.Mini_Project1.service.UserService;
import com.example.Mini_Project1.utils.JwtTokenUtils;
import java.net.URI;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> googleLoginSuccess(OAuth2AuthenticationToken authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication failed");
        }

        OAuth2User oauth2User = authentication.getPrincipal();
        try {
            TokenResponse tokenResponse = userService.googleLogin(oauth2User);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:3000/login?accessToken="
                            + tokenResponse.getAccessToken() + "&refreshToken="
                            + tokenResponse.getRefreshToken()))
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Login failed: " + e.getMessage());
        }
    }

    @GetMapping("/google-login-failure")
    public ResponseEntity<String> googleLoginFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Google login failed");
    }

}
