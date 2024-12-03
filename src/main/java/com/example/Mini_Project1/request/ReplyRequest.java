package com.example.Mini_Project1.request;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
@Getter
@Setter
public class ReplyRequest {
    private UUID commentId;
    private UUID userId;
    @NotBlank(message = "Content cannot be blank")
    private String content;
}
