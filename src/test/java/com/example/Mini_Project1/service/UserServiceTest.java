package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Token;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.repository.TokenRepository;
import com.example.Mini_Project1.repository.UserRepository;
import com.example.Mini_Project1.request.user.CreateUserRequest;
import com.example.Mini_Project1.request.user.LoginRequest;
import com.example.Mini_Project1.request.user.UpdateUserRequest;
import com.example.Mini_Project1.response.user.TokenResponse;
import com.example.Mini_Project1.response.user.UserResponse;
import com.example.Mini_Project1.utils.JwtTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;





@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private TokenRepository tokenRepository;

  @Mock
  private ModelMapper modelMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtTokenUtils jwtTokenUtils;

  @InjectMocks
  private UserService userService;

  private User user;
  private UserResponse userResponse;
  private CreateUserRequest createUserRequest;
  private UpdateUserRequest updateUserRequest;
  private LoginRequest loginRequest;
  private Token token;
  private TokenResponse tokenResponse;

  @BeforeEach
  public void setUp() {
    user = new User();
    user.setId("1");
    user.setName("John Doe");
    user.setEmail("john.doe@example.com");
    user.setPassword("password");
    user.setCreatedDate(new Date());
    user.setUpdatedDate(new Date());

    userResponse = new UserResponse();
    userResponse.setId("1");
    userResponse.setName("John Doe");

    createUserRequest = new CreateUserRequest();
    createUserRequest.setName("John Doe");
    createUserRequest.setEmail("john.doe@example.com");
    createUserRequest.setPassword("password");

    updateUserRequest = new UpdateUserRequest();
    updateUserRequest.setName("John Doe Updated");

    loginRequest = new LoginRequest();
    loginRequest.setEmail("john.doe@example.com");
    loginRequest.setPassword("password");

    token = new Token();
    token.setUser(user);
    token.setToken("refreshToken");
    token.setType("REFRESH");
    token.setExpiredTime(new Date());

    tokenResponse = new TokenResponse();
    tokenResponse.setAccessToken("accessToken");
    tokenResponse.setRefreshToken("refreshToken");
  }

  @Test
  public void testGetAllUsers() {
    when(userRepository.findAll()).thenReturn(Arrays.asList(user));
    when(modelMapper.map(any(User.class), eq(UserResponse.class))).thenReturn(userResponse);

    List<UserResponse> users = userService.getAllUsers();

    assertEquals(1, users.size());
    assertEquals("John Doe", users.get(0).getName());
  }

  @Test
  public void testGetUserById() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(modelMapper.map(any(User.class), eq(UserResponse.class))).thenReturn(userResponse);

    Optional<UserResponse> response = userService.getUserById("1");

    assertTrue(response.isPresent());
    assertEquals("John Doe", response.get().getName());
  }

  @Test
  public void testCreateUser() {
    when(modelMapper.map(any(CreateUserRequest.class), eq(User.class))).thenReturn(user);
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(modelMapper.map(any(User.class), eq(UserResponse.class))).thenReturn(userResponse);

    UserResponse response = userService.createUser(createUserRequest);

    assertEquals("John Doe", response.getName());
  }

  @Test
  public void testFindOrCreateUserByEmail_UserExists() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

    User response = userService.findOrCreateUserByEmail("john.doe@example.com", "John Doe");

    assertEquals("John Doe", response.getName());
  }

  @Test
  public void testFindOrCreateUserByEmail_UserDoesNotExist() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenReturn(user);

    User response = userService.findOrCreateUserByEmail("john.doe@example.com", "John Doe");

    assertEquals("John Doe", response.getName());
  }

  @Test
  public void testLogin_Success() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    when(jwtTokenUtils.createToken(any(User.class))).thenReturn("accessToken");
    when(jwtTokenUtils.createRefreshToken(any(User.class))).thenReturn("refreshToken");
    when(tokenRepository.findByUserId(anyString())).thenReturn(Optional.of(token));
    when(tokenRepository.save(any(Token.class))).thenReturn(token);

    TokenResponse response = userService.login(loginRequest);

    assertEquals("accessToken", response.getAccessToken());
    assertEquals("refreshToken", response.getRefreshToken());
  }

  @Test
  public void testLogin_InvalidPassword() {
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

    assertThrows(RuntimeException.class, () -> userService.login(loginRequest));
  }

  @Test
  public void testRefreshToken_Success() {
    when(jwtTokenUtils.validateToken(anyString())).thenReturn(true);
    when(jwtTokenUtils.getUserIdFromToken(anyString())).thenReturn("1");
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(jwtTokenUtils.createToken(any(User.class))).thenReturn("newAccessToken");
    when(jwtTokenUtils.createRefreshToken(any(User.class))).thenReturn("newRefreshToken");
    when(tokenRepository.save(any(Token.class))).thenReturn(token);

    TokenResponse response = userService.refreshToken("refreshToken");

    assertEquals("newAccessToken", response.getAccessToken());
    assertEquals("newRefreshToken", response.getRefreshToken());
  }

  @Test
  public void testRefreshToken_InvalidToken() {
    when(jwtTokenUtils.validateToken(anyString())).thenReturn(false);

    assertThrows(RuntimeException.class, () -> userService.refreshToken("invalidToken"));
  }

  @Test
  public void testRevokeToken() {
    when(tokenRepository.findByToken(anyString())).thenReturn(Optional.of(token));

    userService.revokeToken("refreshToken");

    verify(tokenRepository, times(1)).delete(token);
  }

@Test
public void testUpdateUser() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(modelMapper.map(any(User.class), eq(UserResponse.class))).thenReturn(userResponse);

    UserResponse response = userService.updateUser("1", updateUserRequest);

    assertEquals("John Doe Updated", response.getName());
    verify(userRepository, times(1)).save(user);
    verify(modelMapper, times(1)).map(user, UserResponse.class);
}
  @Test
  public void testDeleteUser() {
    doNothing().when(userRepository).deleteById(anyString());

    userService.deleteUser("1");

    verify(userRepository, times(1)).deleteById("1");
  }

  @Test
  public void testGoogleLogin() {
    OAuth2User oauth2User = mock(OAuth2User.class);
    when(oauth2User.getAttribute("email")).thenReturn("john.doe@example.com");
    when(oauth2User.getAttribute("name")).thenReturn("John Doe");
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    when(jwtTokenUtils.createToken(any(User.class))).thenReturn("accessToken");
    when(jwtTokenUtils.createRefreshToken(any(User.class))).thenReturn("refreshToken");
    when(tokenRepository.findByUserId(anyString())).thenReturn(Optional.of(token));
    when(tokenRepository.save(any(Token.class))).thenReturn(token);

    TokenResponse response = userService.googleLogin(oauth2User);

    assertEquals("accessToken", response.getAccessToken());
    assertEquals("refreshToken", response.getRefreshToken());
  }
}