package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Token;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, String> {
  Optional<Token> findByToken(String token);

  Optional<Token> findByUserId(String userId);
}
