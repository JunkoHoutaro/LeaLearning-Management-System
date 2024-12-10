package com.example.Mini_Project1.request.QuizzAndQuestion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@NoArgsConstructor
@Getter
@Setter
public class UpdateQuestionRequest {
    @NotNull(message = "Question id is required")
    private UUID questionId;
    private String options;
    private Character correct;
    private String content;
}
