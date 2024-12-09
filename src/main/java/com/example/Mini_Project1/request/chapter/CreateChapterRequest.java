package com.example.Mini_Project1.request.chapter;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateChapterRequest {

    @NotNull(message = "Course id is required")
    private UUID courseId;
    // private String courseId;

    @NotBlank(message = "Chapter name is required")
    private String name;
}
