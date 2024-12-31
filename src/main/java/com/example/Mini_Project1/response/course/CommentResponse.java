package com.example.Mini_Project1.response.course;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CommentResponse {
    private String id;
    private String courseId;
    private String content;
    private String rootComment;
    private Date createdDate;
    private Date updatedDate;
    // Getter và Setter cho message
    @Setter
    @Getter
    private String message;
    public CommentResponse() {}

    // Constructor nhận String
    public CommentResponse(String message) {
        this.message = message;
    }

}
