package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Quizz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizzRepository extends JpaRepository<Quizz, String> {
    boolean existsByName(String name);
    List<Quizz> findByName(String name);
    List<Quizz> findByChapter(Chapter chapter);
    List<Quizz> findByCourse(Course course);
    boolean existsByCourse(Course course);
}