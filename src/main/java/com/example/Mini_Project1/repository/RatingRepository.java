package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Rating;
import com.example.Mini_Project1.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, String> {
    List<Rating> findByCourse(Course course);

    Optional<Rating> findByCourseAndUser(Course course, User user);

}
