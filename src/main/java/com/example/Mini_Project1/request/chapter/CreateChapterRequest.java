package com.example.Mini_Project1.request.chapter;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateChapterRequest {

    @NotNull(message = "Course id is required")
    private UUID courseId;

    @NotNull(message = "Chapter index is required")
    @Positive(message = "Chapter index must be greater than 0.")
    private Integer index;

    @NotBlank(message = "Chapter name is required")
    private String name;
}
