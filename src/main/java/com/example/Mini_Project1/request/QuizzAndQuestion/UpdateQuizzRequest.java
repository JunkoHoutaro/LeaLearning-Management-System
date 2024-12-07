package com.example.Mini_Project1.request.QuizzAndQuestion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@NoArgsConstructor
@Getter
@Setter
public class UpdateQuizzRequest {
    @NotNull(message = "Quizz id is required")
    private UUID quizzId;
    @Positive(message = "Duration must be greater than 0")
    private float duration;
    private String name;

}
