package com.example.Mini_Project1.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class UpdateUserRequest {
  private String name;

  private String email;

  private LocalDate dob;

  private String password;

  private String role;
}
