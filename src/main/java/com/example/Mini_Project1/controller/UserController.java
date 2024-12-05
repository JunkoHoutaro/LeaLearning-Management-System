package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.request.token.TokenRequest;
import com.example.Mini_Project1.request.user.CreateUserRequest;
import com.example.Mini_Project1.request.user.LoginRequest;
import com.example.Mini_Project1.request.user.UpdateUserRequest;
import com.example.Mini_Project1.response.user.TokenResponse;
import com.example.Mini_Project1.response.user.UserResponse;
import com.example.Mini_Project1.service.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  @GetMapping
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
    return userService.getUserById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest createUserRequest) {
    return ResponseEntity.ok(userService.createUser(createUserRequest));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<UserResponse> updateUser(@PathVariable String id,
      @RequestBody UpdateUserRequest updateUserRequest) {
    return ResponseEntity.ok(userService.updateUser(id, updateUserRequest));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable String id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}