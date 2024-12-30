package com.example.Mini_Project1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Payment;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.entity.Voucher;
import com.example.Mini_Project1.response.AdminDashboard.TopCourseResponseRegistration;
import com.example.Mini_Project1.response.AdminDashboard.TopCourseResponseRevenue;
import com.example.Mini_Project1.response.AdminDashboard.TopInstructorResponse;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    @EntityGraph(attributePaths = {"course"})
    List<Payment> findByUserAndStatus(User user, int status);

    @Query("SELECT new com.example.Mini_Project1.response.AdminDashboard.TopCourseResponseRevenue(c.id, c.name, SUM(p.price - p.discount)) "
            + "FROM Payment p JOIN p.course c "
            + "WHERE p.status = 3 OR p.status = 1 "
            + "GROUP BY c.id, c.name "
            + "ORDER BY SUM(p.price - p.discount) DESC") // discount need to review 
    List<TopCourseResponseRevenue> findTopCoursesByRevenue();

    @Query("SELECT new com.example.Mini_Project1.response.AdminDashboard.TopCourseResponseRegistration(c.id, c.name, COUNT(p)) "
            + "FROM Payment p JOIN p.course c "
            + "WHERE p.status = 3 OR p.status = 1 "
            + "GROUP BY c.id, c.name "
            + "ORDER BY COUNT(p) DESC")
    List<TopCourseResponseRegistration> findTopCoursesByRegistration();

    @Query("SELECT new com.example.Mini_Project1.response.AdminDashboard.TopInstructorResponse(u.id, u.name, COUNT(p.id)) "
            + "FROM Payment p JOIN p.user u WHERE u.role = 'instructor' AND (p.status = 3 OR p.status = 1) "
            + "GROUP BY u.id ORDER BY COUNT(p.id) DESC")
    List<TopInstructorResponse> findTopInstructorsByRegistration();

    @EntityGraph(attributePaths = {"user"})
    List<Payment> findByCourseAndStatus(Course course, int status);

    @EntityGraph(attributePaths = {"course"})
    List<Payment> findByUser(User user);

    @EntityGraph(attributePaths = {"user"})
    List<Payment> findByCourse(Course course);

    boolean existsByVoucher(Voucher voucher);

    boolean existsByUserAndCourse(User user, Course course);

    boolean existsByUserIdAndCourseId(String userId, String courseId);

    Payment findByUserIdAndCourseId(String userId, String courseId);

    boolean existsByUserIdAndCourseIdAndStatus(String userId, String courseId, int status);
}
