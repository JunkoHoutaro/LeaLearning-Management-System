// package com.example.Mini_Project1.request;

// import java.time.LocalDate;

// import com.example.Mini_Project1.deserializer.LocalDateDeserializer;
// import com.example.Mini_Project1.validator.dob_constraint.DobConstraint;
// import
// com.example.Mini_Project1.validator.password_constraint.PasswordConstraint;
// import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.Size;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Data
// @AllArgsConstructor
// @NoArgsConstructor
// @Builder
// public class SignUpRequest {
// @NotNull(message = "Email must be not null")
// @NotBlank(message = "Email is mandatory")
// @Email(message = "Email should be valid")
// private String email;

// @NotNull(message = "Password must be not null")
// @PasswordConstraint()
// private String password;

// @NotNull
// @Size(min = 3, max = 50, message = "Username must be between 3 and 50
// characters")
// private String name;

// @NotNull(message = "Date of birth must be not null")
// @JsonDeserialize(using = LocalDateDeserializer.class)
// @DobConstraint(min = 18)
// private LocalDate dob;
// }
