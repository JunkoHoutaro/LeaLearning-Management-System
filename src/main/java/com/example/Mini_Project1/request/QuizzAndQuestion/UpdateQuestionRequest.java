package com.example.Mini_Project1.request.QuizzAndQuestion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
@NoArgsConstructor
@Getter
@Setter
public class UpdateQuestionRequest {
    @NotNull(message = "Question id is required")
    private UUID questionId;

    @Positive(message = "Question index must be greater than 0")
    private Integer index;

    @Size(min = 4, max = 4, message = "Options must contain exactly 4 items")
    private List<String> options;
    private Character correct;
    private String content;
}
