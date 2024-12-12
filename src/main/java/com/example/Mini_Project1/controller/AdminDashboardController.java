package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.response.AdminDashboard.TopCourseResponseRegistration;
import com.example.Mini_Project1.response.AdminDashboard.TopCourseResponseRevenue;
import com.example.Mini_Project1.response.AdminDashboard.TopInstructorResponse;
import com.example.Mini_Project1.service.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class AdminDashboardController {
    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/top-courses/revenue")
    public ResponseEntity<List<TopCourseResponseRevenue>> getTopCoursesByRevenue() {
        List<TopCourseResponseRevenue> topCourses = adminDashboardService.getTopCoursesByRevenue();
        return ResponseEntity.ok(topCourses);
    }

    @GetMapping("/top-courses/registration")
    public ResponseEntity<List<TopCourseResponseRegistration>> getTopCoursesByRegistration() {
        List<TopCourseResponseRegistration> topCourses = adminDashboardService.getTopCoursesByRegistration();
        return ResponseEntity.ok(topCourses);
    }

    @GetMapping("/top-instructors/registration")
    public ResponseEntity<List<TopInstructorResponse>> getTopInstructorsByRegistration() {
        List<TopInstructorResponse> topInstructors = adminDashboardService.getTopInstructorsByRegistration();
        return ResponseEntity.ok(topInstructors);
    }
}