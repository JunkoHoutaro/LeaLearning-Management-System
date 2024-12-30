package com.example.Mini_Project1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Quizz;

public interface QuizzRepository extends JpaRepository<Quizz, String> {

    boolean existsByName(String name);

    List<Quizz> findByName(String name);

    List<Quizz> findByChapter(Chapter chapter);

    List<Quizz> findByCourse(Course course);

    List<Quizz> findByCourseId(String courseId);

    boolean existsByCourse(Course course);

    boolean existsByChapter(Chapter chapter);

}
