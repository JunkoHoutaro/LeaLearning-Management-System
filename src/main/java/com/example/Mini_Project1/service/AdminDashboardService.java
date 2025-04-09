package com.example.Mini_Project1.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.repository.PaymentRepository;
import com.example.Mini_Project1.response.AdminDashboard.TopCourseResponseRegistration;
import com.example.Mini_Project1.response.AdminDashboard.TopCourseResponseRevenue;
import com.example.Mini_Project1.response.AdminDashboard.TopInstructorResponse;

@Service
public class AdminDashboardService {

    private final PaymentRepository paymentRepository;
    private final CourseRepository courseRepository;

    // Constructor duy nhất để inject cả hai repository
    public AdminDashboardService(PaymentRepository paymentRepository, CourseRepository courseRepository) {
        this.paymentRepository = paymentRepository;
        this.courseRepository = courseRepository;
    }

    // Lấy danh sách khóa học top theo doanh thu
    public List<TopCourseResponseRevenue> getTopCoursesByRevenue() {
        return paymentRepository.findTopCoursesByRevenue();
    }

    // Lấy danh sách khóa học top theo số lượng đăng ký
    public List<TopCourseResponseRegistration> getTopCoursesByRegistration() {
        return paymentRepository.findTopCoursesByRegistration();
    }

    // Lấy danh sách giảng viên top theo số lượng đăng ký
    public List<TopInstructorResponse> getTopInstructorsByRegistration() {
        return paymentRepository.findTopInstructorsByRegistration();
    }

    // Lấy top 5 khóa học theo rating
//    public List<TopCourseResponseRating> getTopCoursesByRating() {
//        return RatingRepository.findTopCoursesByRating();
//    }
}
