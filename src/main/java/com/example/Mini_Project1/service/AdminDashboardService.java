package com.example.Mini_Project1.service;

import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.repository.PaymentRepository;
import com.example.Mini_Project1.response.AdminDashboard.TopCourseResponseRating;
import com.example.Mini_Project1.response.AdminDashboard.TopCourseResponseRevenue;
import com.example.Mini_Project1.response.AdminDashboard.TopCourseResponseRegistration;
import com.example.Mini_Project1.response.AdminDashboard.TopInstructorResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminDashboardService {
    private final PaymentRepository paymentRepository;
    private final CourseRepository courseRepository;

    public AdminDashboardService(PaymentRepository paymentRepository, CourseRepository courseRepository) {
        this.paymentRepository = paymentRepository;
        this.courseRepository = courseRepository;
    }

    public List<TopCourseResponseRevenue> getTopCoursesByRevenue() {
        return paymentRepository.findTopCoursesByRevenue();
    }

    public List<TopCourseResponseRegistration> getTopCoursesByRegistration() {
        return paymentRepository.findTopCoursesByRegistration();
    }

    public List<TopInstructorResponse> getTopInstructorsByRegistration() {
        return paymentRepository.findTopInstructorsByRegistration();
    }
}