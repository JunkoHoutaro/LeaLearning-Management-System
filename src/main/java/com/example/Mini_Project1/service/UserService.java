package com.example.Mini_Project1.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Mini_Project1.entity.Token;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.exception.TokenExpiredException;
import com.example.Mini_Project1.exception.UserNotFoundException;
import com.example.Mini_Project1.repository.TokenRepository;
import com.example.Mini_Project1.repository.UserRepository;
import com.example.Mini_Project1.request.user.CreateUserRequest;
import com.example.Mini_Project1.request.user.LoginRequest;
import com.example.Mini_Project1.request.user.UpdateUserRequest;
import com.example.Mini_Project1.response.user.TokenResponse;
import com.example.Mini_Project1.response.user.UserResponse;
import com.example.Mini_Project1.utils.JwtTokenUtils;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final EmailService emailService;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    public Optional<UserResponse> getUserById(String userId) {
        return userRepository.findById(userId).map(user -> modelMapper.map(user, UserResponse.class));
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        User user = modelMapper.map(createUserRequest, User.class);
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        user.setCreatedDate(new Date());
        user.setUpdatedDate(new Date());
        return modelMapper.map(userRepository.save(user), UserResponse.class);
    }

    @Transactional
    public User findOrCreateUserByEmail(String email, String name) {
        try {
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                return existingUser.get();
            }

            // Create new user if not exists
            User newUser = User.builder()
                    .name(name)
                    .email(email)
                    .password(passwordEncoder.encode("example@123"))
                    .role("STUDENT")
                    .createdDate(new Date())
                    .updatedDate(new Date())
                    .build();

            return userRepository.save(newUser);

        } catch (Exception e) {
            // Log the error
            log.error("Error in findOrCreateUserByEmail: {}", e.getMessage());
            throw new RuntimeException("Failed to find or create user: " + e.getMessage());
        }
    }

    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        User user
                = userRepository
                        .findByEmail(loginRequest.getEmail())
                        .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        return getTokenResponse(user);
    }

    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        if (!jwtTokenUtils.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        String userId = jwtTokenUtils.getUserIdFromToken(refreshToken);
        User user
                = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
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

    @Transactional
    public void revokeToken(String refreshToken) {
        Token token
                = tokenRepository
                        .findByToken(refreshToken)
                        .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        tokenRepository.delete(token);
    }

    @Transactional
    public UserResponse updateUser(String id, UpdateUserRequest updateUserRequest) {
        User user
                = userRepository
                        .findById(id)
                        .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (updateUserRequest.getName() != null) {
            user.setName(updateUserRequest.getName());
        }
        if (updateUserRequest.getEmail() != null) {
            user.setEmail(updateUserRequest.getEmail());
        }
        if (updateUserRequest.getDob() != null) {
            user.setDob(updateUserRequest.getDob());
        }
        if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
        }
        if (updateUserRequest.getRole() != null) {
            user.setRole(updateUserRequest.getRole());
        }
        user.setUpdatedDate(new Date());

        return modelMapper.map(userRepository.save(user), UserResponse.class);
    }

    @Transactional
    public void deleteUser(String id) {
        User user
                = userRepository
                        .findById(id)
                        .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    // Google login method
    @Transactional
    public TokenResponse googleLogin(OAuth2User oauth2User) {
        try {
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");

            if (email == null) {
                throw new RuntimeException("Email not provided by Google");
            }

            User user = findOrCreateUserByEmail(email, name);
            return getTokenResponse(user);

        } catch (Exception e) {
            // logger.error("Google login failed: {}", e.getMessage());
            throw new RuntimeException("Google login failed: " + e.getMessage());
        }
    }

    // Helper method to generate JWT token
    private TokenResponse getTokenResponse(User user) {
        String accessToken = jwtTokenUtils.createToken(user);
        String refreshToken = jwtTokenUtils.createRefreshToken(user);

        // Remove existing refresh tokens
        tokenRepository.findByUserIdAndType(user.getId(), "REFRESH")
                .ifPresent(tokenRepository::delete);

        // Save new refresh token
        Token token = Token.builder()
                .user(user)
                .token(refreshToken)
                .type("REFRESH")
                .expiredTime(jwtTokenUtils.getExpirationDate(refreshToken))
                .isBlackListed(0)
                .build();

        tokenRepository.save(token);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void forgotPassword(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String resetToken = jwtTokenUtils.createResetPasswordToken(user);

            Token token = Token.builder()
                    .user(user)
                    .token(resetToken)
                    .type("RESET_PASSWORD")
                    .expiredTime(jwtTokenUtils.getExpirationDate(resetToken))
                    .isBlackListed(0)
                    .build();

            tokenRepository.findByUserIdAndType(user.getId(), "RESET_PASSWORD")
                    .ifPresent(tokenRepository::delete);

            tokenRepository.save(token);

            emailService.sendResetPasswordEmail(user.getEmail(), resetToken, user.getName());

        } catch (Exception e) {
            log.error("Error in forgot password flow: {}", e.getMessage());
            throw new RuntimeException("Failed to process forgot password request: " + e.getMessage());
        }
    }

    @Transactional
    public void resetPassword(String resetToken, String newPassword) {
        if (!jwtTokenUtils.validateToken(resetToken)) {
            throw new TokenExpiredException("Reset token is invalid or expired");
        }

        String userId = jwtTokenUtils.getUserIdFromToken(resetToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Revoke the reset token
        tokenRepository.findByToken(resetToken)
                .ifPresent(tokenRepository::delete);
    }

}
