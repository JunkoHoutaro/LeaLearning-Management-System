// package com.example.Mini_Project1.request;

// import
// com.example.Mini_Project1.validator.password_constraint.PasswordConstraint;

// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Data
// @Builder
// @AllArgsConstructor
// @NoArgsConstructor
// public class LoginRequest {
// @NotNull(message = "Email must be not null")
// @NotBlank(message = "Email is mandatory")
// @Email(message = "Email should be valid")
// private String email;

// @NotNull(message = "Password must be not null")
// @PasswordConstraint()
// private String password;
// }
