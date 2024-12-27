package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Question;
import com.example.Mini_Project1.entity.Quizz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, String> {
    boolean existsByContentAndQuizz(String content, Quizz quizz);
    boolean existsByContent (String content);
    List<Question> findByCorrect(Character correct);
    List<Question> findByQuizz(Quizz quizz);
    List<Question> findByQuizzAndCorrect(Quizz quizz, Character correct);
    boolean existsByQuizzIdAndIndex(String quizzId, Integer index);
}