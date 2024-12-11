package com.example.Mini_Project1.request.QuizzAndQuestion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class CreateQuestionRequest {
    @NotNull(message = "Quizz id is required")
    private UUID quizzId;

    @NotNull(message = "Options are required")
    @Size(min = 4, max = 4, message = "Options must contain exactly 4 items")
    private List<String> options;

    @NotNull(message = "Correct answer must be provided")
    private Character correct;

    @NotBlank(message = "Content is required")
    private String content;
}
