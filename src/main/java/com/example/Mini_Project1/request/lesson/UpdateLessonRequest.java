package com.example.Mini_Project1.request.lesson;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "Lesson name is required")
    private String name;

    private String resourceUrl;

    private String videoUrl;

    private Integer isDemo;

    // @NotNull(message = "Chapter id is required")
    // private UUID chapterId;
}
