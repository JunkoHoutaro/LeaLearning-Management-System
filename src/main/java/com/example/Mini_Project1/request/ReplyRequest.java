package com.example.Mini_Project1.request;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
@Getter
@Setter
public class ReplyRequest {
    private String commentId;
    private String userId;
    private String content;
}
