package com.example.Mini_Project1.request.course;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private String courseId;
    private String userId;
    @NotNull(message = "Content cannot be null")
    private String content;
}
