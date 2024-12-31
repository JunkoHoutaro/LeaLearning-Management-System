package com.example.Mini_Project1.request.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CommentRequest {

    @NotNull(message = "Course ID is required")
    private UUID courseId;

    @NotBlank(message = "Comment content cannot be empty")
    private String content;
}
