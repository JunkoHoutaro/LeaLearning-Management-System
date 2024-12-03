package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.request.CommentRequest;
import com.example.Mini_Project1.request.ReplyRequest;
import com.example.Mini_Project1.response.CommentResponse;
import com.example.Mini_Project1.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.createComment(request));
    }

    @PostMapping("/reply")
    public ResponseEntity<CommentResponse> replyToComment(@RequestBody ReplyRequest request) {
        return ResponseEntity.ok(commentService.replyToComment(request));
    }
}
