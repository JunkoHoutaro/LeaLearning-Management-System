package com.example.Mini_Project1.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class CommentRequest {
    private UUID courseId;
    private UUID userId;

    @NotBlank(message = "Content cannot be blank")
    private String content;
}
