package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ScoreRepository extends JpaRepository<Score, String>, JpaSpecificationExecutor<Score> {
}