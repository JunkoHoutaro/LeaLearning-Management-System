package com.example.Mini_Project1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Mini_Project1.entity.Question;
import com.example.Mini_Project1.entity.Quizz;

public interface QuestionRepository extends JpaRepository<Question, String> {

    boolean existsByContentAndQuizz(String content, Quizz quizz);

    boolean existsByContent(String content);

    List<Question> findByCorrect(Character correct);

    List<Question> findByQuizz(Quizz quizz);

    List<Question> findByQuizzAndCorrect(Quizz quizz, Character correct);

    boolean existsByQuizzIdAndIndex(String quizzId, Integer index);

    @Query("SELECT CASE WHEN COUNT(q) > 0 THEN TRUE ELSE FALSE END FROM Question q WHERE q.quizz.id = :quizzId AND q.index = :index AND q.id <> :currentQuestionId")
    boolean existsByQuizzIdAndIndexExcludingCurrent(@Param("quizzId") String quizzId, @Param("index") Integer index, @Param("currentQuestionId") String currentQuestionId);

    @Query("SELECT CASE WHEN COUNT(q) > 0 THEN TRUE ELSE FALSE END FROM Question q WHERE q.content = :content AND q.quizz = :quizz AND q.id <> :currentQuestionId")
    boolean existsByContentAndQuizzExcludingCurrent(@Param("content") String content, @Param("quizz") Quizz quizz, @Param("currentQuestionId") String currentQuestionId);

}
