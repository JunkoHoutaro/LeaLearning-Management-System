package com.example.Mini_Project1.request.QuizzAndQuestion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class CreateQuestionRequest {
    @NotNull(message = "Quizz id is required")
    private UUID quizzId;

    @NotBlank(message = "Options are required")
    private String options;

    @NotNull(message = "Correct answer must be provided")
    private Character correct;

    @NotBlank(message = "Content is required")
    private String content;
}
