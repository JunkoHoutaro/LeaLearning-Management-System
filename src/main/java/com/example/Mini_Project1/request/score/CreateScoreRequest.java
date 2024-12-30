package com.example.Mini_Project1.request.score;

import java.util.Map;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateScoreRequest {

    @NotNull(message = "Quiz id is required")
    private UUID quizzId;

    @NotNull(message = "User answers are required")
    private Map<String, Character> answers;
}
