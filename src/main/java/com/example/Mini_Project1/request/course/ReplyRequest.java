package com.example.Mini_Project1.request.course;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyRequest {
    private String rootCommentId;
    private String userId;
    private String content;
}
