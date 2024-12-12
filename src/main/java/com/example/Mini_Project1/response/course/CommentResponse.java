package com.example.Mini_Project1.response.course;
import lombok.Data;

import java.util.Date;

@Data
public class CommentResponse {
    private String id;
    private String courseId;
    private String userId;
    private String content;
    private String rootComment;
    private Date createdDate;
    private Date updatedDate;
}