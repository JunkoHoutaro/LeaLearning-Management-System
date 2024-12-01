package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}