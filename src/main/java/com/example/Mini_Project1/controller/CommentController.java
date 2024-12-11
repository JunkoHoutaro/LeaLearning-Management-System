package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.request.course.CommentRequest;
import com.example.Mini_Project1.request.course.ReplyRequest;
import com.example.Mini_Project1.request.course.UpdateCommentRequest;
import com.example.Mini_Project1.response.course.CommentResponse;
import com.example.Mini_Project1.service.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/comments")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // Comment
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest commentRequest) {
        CommentResponse commentResponse = commentService.createComment(commentRequest);
        return ResponseEntity.ok(commentResponse);
    }

    // Reply
    @PostMapping("/reply")
    public ResponseEntity<CommentResponse> replyToComment(@RequestBody ReplyRequest replyRequest) {
        CommentResponse commentResponse = commentService.replyToComment(replyRequest);
        return ResponseEntity.ok(commentResponse);
    }

    // Get all comments
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getAllComments() {
        List<CommentResponse> commentResponses = commentService.getAllComments();
        return ResponseEntity.ok(commentResponses);
    }

    // Get comments by courseId
    @GetMapping("/{courseId}")
    public ResponseEntity<?> getCommentsByCourseId(@PathVariable String courseId) {
        try {
            UUID courseUUID = UUID.fromString(courseId);
            List<CommentResponse> commentResponses = commentService.getCommentsByCourseId(courseUUID.toString());
            return ResponseEntity.ok(commentResponses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid UUID format for courseId");
        }
    }

    // Update comment
    @PatchMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable String commentId, @RequestBody @Valid UpdateCommentRequest updateCommentRequest) {
        try {
            UUID commentUUID = UUID.fromString(commentId);
            CommentResponse commentResponse = commentService.updateCommentContent(commentUUID, updateCommentRequest);
            return ResponseEntity.ok(commentResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid UUID format for commentId");
        }
    }

    // Delete comment
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String commentId) {
        try {
            UUID commentUUID = UUID.fromString(commentId);
            String message = commentService.deleteComment(commentUUID);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid UUID format for commentId");
        }
    }
}
