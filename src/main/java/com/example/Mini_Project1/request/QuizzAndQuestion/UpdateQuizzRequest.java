package com.example.Mini_Project1.request.QuizzAndQuestion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@NoArgsConstructor
@Getter
@Setter
public class UpdateQuizzRequest {
    @NotNull(message = "Quizz id is required")
    private String quizzId;

    @NotNull(message = "Duration is required")
    @PositiveOrZero(message = "Duration must be greater than or equal to 0")
    private float duration;

    @NotBlank(message = "Name is required")
    private String name;
}
