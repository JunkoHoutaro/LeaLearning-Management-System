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

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.courseId = comment.getCourse().getId();
        this.userId = comment.getUser().getId();
        this.content = comment.getContent();
        this.rootCommentId = comment.getRootCommentId();
        this.createdDate = comment.getCreatedDate();
        this.updatedDate = comment.getUpdatedDate();
    }
}