package com.example.Mini_Project1.response.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TokenResponse {
  private String accessToken;
  private String refreshToken;
}
