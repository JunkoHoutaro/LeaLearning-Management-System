package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, String> {
  List<Course> findCourseByStatus(int status);

  List<Course> findCourseByNameContainingIgnoreCaseAndStatusNot(String name, int status);

  List<Course> findCourseByUser(User user);

  boolean existsByNameAndUser(String name, User user);
}
