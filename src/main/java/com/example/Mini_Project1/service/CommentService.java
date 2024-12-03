package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Comment;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.repository.CommentRepository;
import com.example.Mini_Project1.request.CommentRequest;
import com.example.Mini_Project1.request.ReplyRequest;
import com.example.Mini_Project1.response.CommentResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    // private final CourseRepository courseRepository;
    // private final UserRepository userRepository;

    public CommentResponse createComment(CommentRequest request) {
        // Course course = courseRepository.findById(request.getCourseId()).orElseThrow();
        // User user = userRepository.findById(request.getUserId()).orElseThrow();
        Comment newComment = Comment.builder()
                .course(null)
                .user(null)
                .content(request.getContent())
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();

        commentRepository.save(newComment);

        return new CommentResponse(newComment);
    }


    public CommentResponse replyToComment(ReplyRequest request) {
        // Comment parentComment = commentRepository.findById(request.getCommentId()).orElseThrow();
        // User user = userRepository.findById(request.getUserId()).orElseThrow();

        Comment reply = Comment.builder()
                .course(null)
                .user(null)
                .content(request.getContent())
                .rootCommentId(String.valueOf(request.getCommentId()))
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();

        commentRepository.save(reply);
        return new CommentResponse(reply);
    }
}
