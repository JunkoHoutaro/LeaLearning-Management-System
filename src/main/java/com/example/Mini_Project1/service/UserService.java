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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final ModelMapper modelMapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenUtils jwtTokenUtils;

  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream()
        .map(user -> modelMapper.map(user, UserResponse.class))
        .collect(Collectors.toList());
  }

  public Optional<UserResponse> getUserById(String id) {
    return userRepository.findById(id)
        .map(user -> modelMapper.map(user, UserResponse.class));
  }

  public UserResponse createUser(CreateUserRequest createUserRequest) {
    User user = modelMapper.map(createUserRequest, User.class);
    user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
    user.setCreatedDate(new Date());
    user.setUpdatedDate(new Date());
    return modelMapper.map(userRepository.save(user), UserResponse.class);
  }

  public TokenResponse login(LoginRequest loginRequest) {
    User user = userRepository.findByEmail(loginRequest.getEmail())
        .orElseThrow(() -> new RuntimeException("Invalid email or password"));
    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
      throw new RuntimeException("Invalid email or password");
    }
    String accessToken = jwtTokenUtils.createToken(user);
    String refreshToken = jwtTokenUtils.createRefreshToken(user);

    Optional<Token> existingToken = tokenRepository.findByUserId(user.getId());
    if (existingToken.isPresent()) {
      Token token = existingToken.get();
      token.setToken(refreshToken);
      token.setExpiredTime(jwtTokenUtils.getExpirationDate(refreshToken));
      tokenRepository.save(token);
    } else {
      Token token = new Token();
      token.setUser(user);
      token.setToken(refreshToken);
      token.setType("REFRESH");
      token.setExpiredTime(jwtTokenUtils.getExpirationDate(refreshToken));
      tokenRepository.save(token);
    }

    TokenResponse tokenResponse = new TokenResponse();
    tokenResponse.setAccessToken(accessToken);
    tokenResponse.setRefreshToken(refreshToken);
    return tokenResponse;
  }

  public TokenResponse refreshToken(String refreshToken) {
    if (!jwtTokenUtils.validateToken(refreshToken)) {
      throw new RuntimeException("Invalid refresh token");
    }
    String userId = jwtTokenUtils.getUserIdFromToken(refreshToken);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    String newAccessToken = jwtTokenUtils.createToken(user);
    String newRefreshToken = jwtTokenUtils.createRefreshToken(user);
    Token token = new Token();
    token.setUser(user);
    token.setToken(newRefreshToken);
    token.setType("REFRESH");
    token.setExpiredTime(jwtTokenUtils.getExpirationDate(newRefreshToken));
    tokenRepository.save(token);
    TokenResponse tokenResponse = new TokenResponse();
    tokenResponse.setAccessToken(newAccessToken);
    tokenResponse.setRefreshToken(newRefreshToken);
    return tokenResponse;
  }

  public void revokeToken(String refreshToken) {
    Token token = tokenRepository.findByToken(refreshToken)
        .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
    tokenRepository.delete(token);
  }

  public UserResponse updateUser(String id, UpdateUserRequest updateUserRequest) {
    User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    modelMapper.map(updateUserRequest, user);
    if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()) {
      user.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
    }
    user.setUpdatedDate(new Date());
    return modelMapper.map(userRepository.save(user), UserResponse.class);
  }

  public void deleteUser(String id) {
    userRepository.deleteById(id);
  }
}