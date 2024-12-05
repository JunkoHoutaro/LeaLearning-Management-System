package com.example.Mini_Project1.response;
import com.example.Mini_Project1.entity.Comment;
import lombok.Data;

import java.util.Date;

@Data
public class CommentResponse {
    private String id;
    private String courseId;
    private String userId;
    private String content;
    private String rootCommentId;
    private Date createdDate;
    private Date updatedDate;
}