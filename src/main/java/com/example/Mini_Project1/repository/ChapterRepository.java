package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, String> {

    List<Chapter> findByCourse(Course course);

    boolean existsByNameAndCourse(String name, Course course);
}
