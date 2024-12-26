package com.example.Mini_Project1.request.lesson;

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
public class UpdateLessonRequest {

    @NotNull(message = "Lesson id is required")
    private UUID lessonId;

    @Positive(message = "Lesson index must be greater than 0")
    private Integer index;

    private String name;

    private String resourceUrl;

    private String videoUrl;

    private Integer isDemo;
}
