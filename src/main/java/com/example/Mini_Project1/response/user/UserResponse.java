package com.example.Mini_Project1.response.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class UserResponse {
  private String id;
  private String name;
  private String email;
  private LocalDate dob;
  private String role;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createdDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date updatedDate;
}
