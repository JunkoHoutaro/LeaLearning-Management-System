package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, String> {

}