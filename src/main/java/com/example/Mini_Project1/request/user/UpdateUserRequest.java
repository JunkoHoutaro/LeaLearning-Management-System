package com.example.Mini_Project1.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class UpdateUserRequest {
  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;

  private LocalDate dob;

  private String password;

  @NotBlank(message = "Role is required")
  private String role;
}
