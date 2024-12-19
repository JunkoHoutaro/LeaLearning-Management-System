package com.example.Mini_Project1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.Mini_Project1.entity.Course;

import java.util.List;

@Repository
public interface RatingCourseRepository extends JpaRepository<Course, String> {
    List<Course> findByName(String name);
}
