package com.example.Mini_Project1.request.lesson;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class CreateLessonRequest {

    @NotNull(message = "Chapter id is required")
    private UUID chapterId;

    @NotBlank(message = "Lesson name is required")
    private String name;

    @NotBlank(message = "Resource URL is required")
    private String resourceUrl;

    @NotBlank(message = "Video URL is required")
    private String videoUrl;

    @NotNull(message = "IsDemo is required")
    private Integer isDemo;
}
