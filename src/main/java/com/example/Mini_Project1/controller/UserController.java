package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.exception.UserNotFoundException;
import com.example.Mini_Project1.request.user.CreateUserRequest;
import com.example.Mini_Project1.request.user.UpdateUserRequest;
import com.example.Mini_Project1.response.user.UserResponse;
import com.example.Mini_Project1.service.UserService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.username")
  public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
    return userService
        .getUserById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest createUserRequest) {
    return ResponseEntity.ok(userService.createUser(createUserRequest));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.username")
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable String id, @RequestBody UpdateUserRequest updateUserRequest) {
    return ResponseEntity.ok(userService.updateUser(id, updateUserRequest));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.username")
  public ResponseEntity<Void> deleteUser(@PathVariable String id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
