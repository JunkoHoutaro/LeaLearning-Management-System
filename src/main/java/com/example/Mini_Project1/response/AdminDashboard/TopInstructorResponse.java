package com.example.Mini_Project1.response.AdminDashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopInstructorResponse {
    private String id;
    private String instructorName;
    private Long registrationCount;
}
