package com.example.Mini_Project1.request.QuizzAndQuestion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@NoArgsConstructor
@Getter
@Setter
public class UpdateQuestionRequest {
    @NotNull(message = "Question id is required")
    private String questionId;

    @NotBlank(message = "Options are required")
    private String options;

    @NotNull(message = "Correct answer must be provided")
    private Character correct;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Quizz id is required")
    private String quizzId;
}
