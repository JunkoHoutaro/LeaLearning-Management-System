package com.example.Mini_Project1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.RatingHistory;
import com.example.Mini_Project1.entity.User;

@Repository
public interface RatingHistoryRepository extends JpaRepository<RatingHistory, String> {

    List<RatingHistory> findByCourse(Course course);

    List<RatingHistory> findByCourseAndUser(Course course, User user);

    boolean existsByCourseAndUser(Course course, User user);

}
