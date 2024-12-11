package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, String> {
  List<Course> findCourseByStatus(int status);
  List<Course> findCourseByNameContainingIgnoreCase(String name);
  List<Course> findCourseByNameContainingIgnoreCaseAndStatus(String name, int status);
  List<Course> findCourseByUser(User user);
  List<Course> findCourseByUserAndStatus(User user, int status);
  boolean existsByNameAndUser(String name, User user);

  // Optional<Course> findById(UUID courseId);
}
