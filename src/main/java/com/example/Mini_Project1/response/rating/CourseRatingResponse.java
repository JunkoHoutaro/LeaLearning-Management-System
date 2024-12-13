package com.example.Mini_Project1.response.rating;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseRatingResponse {
    private String courseName;
    private double averageRating;
    private List<RatingResponse> ratings;
}
