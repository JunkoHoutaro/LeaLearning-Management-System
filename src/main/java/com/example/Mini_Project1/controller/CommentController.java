package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.exception.ErrorResponse;
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
    public ResponseEntity<?> createComment(
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            boolean isUserAuthorized = paymentService.isUserAuthorizedToComment(
                    userDetails.getUsername(), String.valueOf(request.getCourseId()));
            if (!isUserAuthorized) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse(403, "Forbidden", "User is not authorized to comment."));
            }

            CommentResponse commentResponse = commentService.createComment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Internal Server Error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/reply")
    @Operation(summary = "Reply to a comment")
    public ResponseEntity<?> replyToComment(
            @Valid @RequestBody ReplyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            CommentResponse response = commentService.replyToComment(request, userDetails);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Internal Server Error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "Get comments by course ID")
    public ResponseEntity<?> getCommentsByCourseId(@PathVariable UUID courseId) {
        try {
            List<CommentResponse> comments = commentService.getCommentsByCourseId(courseId.toString());
            if (comments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(404, "Not Found", "No comments found for the specified course ID"));
            }
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Internal Server Error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get all comments")
    public ResponseEntity<?> getAllComments() {
        try {
            List<CommentResponse> allComments = commentService.getAllComments();
            if (allComments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(404, "Not Found", "No comments found"));
            }
            return ResponseEntity.ok(allComments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Internal Server Error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "Update comment content")
    @ApiResponse(responseCode = "200", description = "Comment updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid comment ID format",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<?> updateComment(
            @PathVariable UUID commentId,
            @Valid @RequestBody UpdateCommentRequest request) {
        try {
            CommentResponse commentResponse = commentService.updateCommentContent(commentId, request);
            return ResponseEntity.ok(commentResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Bad Request", "Invalid UUID format for commentId"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Internal Server Error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete a comment")
    @ApiResponse(responseCode = "200", description = "Comment deleted successfully")
    @ApiResponse(responseCode = "400", description = "Invalid comment ID format",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<?> deleteComment(@PathVariable UUID commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Bad Request", "Invalid UUID format for commentId"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(500, "Internal Server Error", "An unexpected error occurred: " + e.getMessage()));
        }
    }
}
