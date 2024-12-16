package com.example.Mini_Project1.request.rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RatingCreateRequest {

    @Min(value = 1, message = "Rating must be greater than or equal to 1")
    @Max(value = 5, message = "Rating must be less than or equal to 5")
    private float rating;

    private String feedback;

    @NotNull(message = "Course ID must not be null")
    private String courseId;

    @NotNull(message = "User ID must not be null")
    private String userId;
}
