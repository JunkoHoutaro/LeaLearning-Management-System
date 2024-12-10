package com.example.Mini_Project1.request.course;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateCommentRequest {
    @NotBlank(message = "Content is required")
    private String content;

}
