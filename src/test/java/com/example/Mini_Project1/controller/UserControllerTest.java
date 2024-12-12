package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.request.user.CreateUserRequest;
import com.example.Mini_Project1.request.user.UpdateUserRequest;
import com.example.Mini_Project1.response.user.UserResponse;
import com.example.Mini_Project1.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UserControllerTest {

  @Mock
  private UserService userService;

  @InjectMocks
  private UserController userController;

  private MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
  }

  @Test
  public void testGetAllUsers() {
    UserResponse user1 = new UserResponse();
    user1.setId("1");
    user1.setName("John Doe");

    UserResponse user2 = new UserResponse();
    user2.setId("2");
    user2.setName("Jane Doe");

    List<UserResponse> users = Arrays.asList(user1, user2);

    when(userService.getAllUsers()).thenReturn(users);

    ResponseEntity<List<UserResponse>> response = userController.getAllUsers();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().size());
  }

  @Test
  public void testGetUserById() {
    UserResponse user = new UserResponse();
    user.setId("1");
    user.setName("John Doe");

    when(userService.getUserById(anyString())).thenReturn(Optional.of(user));

    ResponseEntity<UserResponse> response = userController.getUserById("1");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("John Doe", response.getBody().getName());
  }

  @Test
  public void testGetUserByIdNotFound() {
    when(userService.getUserById(anyString())).thenReturn(Optional.empty());

    ResponseEntity<UserResponse> response = userController.getUserById("1");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void testCreateUser() {
    CreateUserRequest createUserRequest = new CreateUserRequest();
    createUserRequest.setName("John Doe");
    createUserRequest.setEmail("john.doe@example.com");
    createUserRequest.setPassword("password");

    UserResponse userResponse = new UserResponse();
    userResponse.setId("1");
    userResponse.setName("John Doe");

    when(userService.createUser(any(CreateUserRequest.class))).thenReturn(userResponse);

    ResponseEntity<UserResponse> response = userController.createUser(createUserRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("John Doe", response.getBody().getName());
  }

  @Test
  public void testUpdateUser() {
    UpdateUserRequest updateUserRequest = new UpdateUserRequest();
    updateUserRequest.setName("John Doe Updated");

    UserResponse userResponse = new UserResponse();
    userResponse.setId("1");
    userResponse.setName("John Doe Updated");

    when(userService.updateUser(anyString(), any(UpdateUserRequest.class))).thenReturn(userResponse);

    ResponseEntity<UserResponse> response = userController.updateUser("1", updateUserRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("John Doe Updated", response.getBody().getName());
  }

  @Test
  public void testDeleteUser() {
    ResponseEntity<Void> response = userController.deleteUser("1");

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }
}