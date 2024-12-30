package com.example.Mini_Project1.request.score;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class CreateScoreRequest {
    @NotNull(message = "User id is required")
    private UUID userId;

    @NotNull(message = "Quiz id is required")
    private UUID quizId;

    @NotNull(message = "User answers are required")
    private Map<UUID,Character> answers;
}
