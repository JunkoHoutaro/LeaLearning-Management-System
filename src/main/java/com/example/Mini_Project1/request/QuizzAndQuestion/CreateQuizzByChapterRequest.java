package com.example.Mini_Project1.request.QuizzAndQuestion;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class CreateQuizzByChapterRequest {
    @NotNull(message = "Chapter id is required")
    private UUID chapterId;

    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be greater than 0")
    private float duration;

    @NotBlank(message = "Name is required")
    private String name;
}
