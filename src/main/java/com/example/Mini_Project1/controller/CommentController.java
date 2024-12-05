package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.request.CommentRequest;
import com.example.Mini_Project1.request.ReplyRequest;
import com.example.Mini_Project1.response.CommentResponse;
import com.example.Mini_Project1.service.CommentService;
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

    // Tạo mới comment
    @PostMapping("/create")
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest commentRequest) {
        CommentResponse commentResponse = commentService.createComment(commentRequest);
        return ResponseEntity.ok(commentResponse);
    }

    // Trả lời comment
    @PostMapping("/reply")
    public ResponseEntity<CommentResponse> replyToComment(@RequestBody ReplyRequest replyRequest) {
        CommentResponse commentResponse = commentService.replyToComment(replyRequest);
        return ResponseEntity.ok(commentResponse);
    }

    // Lấy tất cả comment
    @GetMapping("/all")
    public ResponseEntity<List<CommentResponse>> getAllComments() {
        List<CommentResponse> commentResponses = commentService.getAllComments();
        return ResponseEntity.ok(commentResponses);
    }

    // Lấy danh sách comment của một khóa học (course)
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getCommentsByCourseId(@PathVariable String courseId) {
        try {
            UUID courseUUID = UUID.fromString(courseId);
            List<CommentResponse> commentResponses = commentService.getCommentsByCourseId(String.valueOf(courseUUID));
            return ResponseEntity.ok(commentResponses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid UUID format for courseId");
        }
    }

    // Cập nhật comment
    @PutMapping("/update/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable String commentId, @RequestBody CommentRequest commentRequest) {
        try {
            UUID commentUUID = UUID.fromString(commentId); // Chuyển đổi String thành UUID
            CommentResponse commentResponse = commentService.updateComment(commentUUID, commentRequest);
            return ResponseEntity.ok(commentResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid UUID format for commentId");
        }
    }

    // Xóa comment
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String commentId) {
        try {
            UUID commentUUID = UUID.fromString(commentId); // Chuyển đổi String thành UUID
            String message = commentService.deleteComment(commentUUID);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid UUID format for commentId");
        }
    }
}
