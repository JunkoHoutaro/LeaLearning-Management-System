package com.example.Mini_Project1.request.QuizzAndQuestion;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class CreateQuizzRequest {
//    @NotNull(message = "Course id is required")
//    private String courseId;

    @NotNull(message = "Duration is required")
    @PositiveOrZero(message = "Duration must be greater than or equal to 0")
    private float duration;

    @NotBlank(message = "Name is required")
    private String name;
}
