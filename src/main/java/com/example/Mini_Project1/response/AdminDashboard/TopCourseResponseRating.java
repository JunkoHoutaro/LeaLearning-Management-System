package com.example.Mini_Project1.response.AdminDashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopCourseResponseRating {
    private String courseId;
    private String courseName;
    private Double averageRating;
}
