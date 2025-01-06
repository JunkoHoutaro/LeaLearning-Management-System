package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.exception.AuthenticationException;
import com.example.Mini_Project1.exception.ErrorResponse;
import com.example.Mini_Project1.exception.ResourceNotFoundException;
import com.example.Mini_Project1.request.course.CommentRequest;
import com.example.Mini_Project1.request.course.ReplyRequest;
import com.example.Mini_Project1.request.course.UpdateCommentRequest;
import com.example.Mini_Project1.response.course.CommentResponse;
import com.example.Mini_Project1.service.CommentService;
import com.example.Mini_Project1.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Create a new comment")
    @ApiResponse(responseCode = "201", description = "Comment created successfully")
    @ApiResponse(responseCode = "403", description = "User not authorized",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<CommentResponse> createComment(
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean isUserAuthorized = paymentService.isUserAuthorizedToComment(
                userDetails.getUsername(), String.valueOf(request.getCourseId()));

        if (!isUserAuthorized) {
            throw new AuthenticationException("User is not authorized to comment.");
        }

        CommentResponse commentResponse = commentService.createComment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);
    }

    @PostMapping("/reply")
    @Operation(summary = "Reply to a comment")
    @ApiResponse(responseCode = "200", description = "Comment replied successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Parent comment not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<CommentResponse> replyToComment(
            @Valid @RequestBody ReplyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        CommentResponse response = commentService.replyToComment(request, userDetails);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "Get comments by course ID")
    @ApiResponse(responseCode = "200", description = "List of comments returned successfully")
    @ApiResponse(responseCode = "404", description = "No comments found for the course ID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<CommentResponse>> getCommentsByCourseId(
            @PathVariable String courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<CommentResponse> comments = commentService.getCommentsByCourseId(courseId, userDetails);
        if (comments.isEmpty()) {
            throw new ResourceNotFoundException("No comments found for the specified course ID.");
        }
        return ResponseEntity.ok(comments);
    }

    @GetMapping
    @Operation(summary = "Get all comments")
    @ApiResponse(responseCode = "200", description = "List of all comments returned successfully")
    @ApiResponse(responseCode = "404", description = "No comments found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<CommentResponse>> getAllComments(@AuthenticationPrincipal UserDetails userDetails) {
        List<CommentResponse> allComments = commentService.getAllComments(userDetails);
        if (allComments.isEmpty()) {
            throw new ResourceNotFoundException("No comments found.");
        }
        return ResponseEntity.ok(allComments);
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "Update comment content")
    @ApiResponse(responseCode = "200", description = "Comment updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid comment ID format",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Comment not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable UUID commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        CommentResponse commentResponse = commentService.updateCommentContent(commentId, request, userDetails);
        return ResponseEntity.ok(commentResponse);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete a comment")
    @ApiResponse(responseCode = "200", description = "Comment deleted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid comment ID format",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Comment not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<String> deleteComment(@PathVariable UUID commentId,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails);
        return ResponseEntity.ok("Comment deleted successfully.");
    }
}
