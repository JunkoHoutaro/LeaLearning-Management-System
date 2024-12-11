package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Payment;
import com.example.Mini_Project1.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
  @EntityGraph(attributePaths = {"course"})
  List<Payment> findByUserAndStatus(User user, int status);
  @EntityGraph(attributePaths = {"user"})
  List<Payment> findByCourseAndStatus(Course course, int status);
  @EntityGraph(attributePaths = {"course"})
  List<Payment> findByUser(User user);
  @EntityGraph(attributePaths = {"user"})
  List<Payment> findByCourse(Course course);
}
