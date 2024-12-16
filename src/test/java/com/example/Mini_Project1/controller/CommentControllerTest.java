package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.request.course.CommentRequest;
import com.example.Mini_Project1.request.course.ReplyRequest;
import com.example.Mini_Project1.request.course.UpdateCommentRequest;
import com.example.Mini_Project1.response.course.CommentResponse;
import com.example.Mini_Project1.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateComment() {
        // Mock request
        CommentRequest request = new CommentRequest();
        request.setCourseId(UUID.randomUUID().toString());
        request.setUserId(UUID.randomUUID().toString());
        request.setContent("This is a test comment");

        // Mock response
        CommentResponse response = new CommentResponse();
        response.setContent("This is a test comment");

        when(commentService.createComment(any(CommentRequest.class))).thenReturn(response);

        // Call controller method
        ResponseEntity<CommentResponse> result = commentController.createComment(request);

        // Verify result
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("This is a test comment", result.getBody().getContent());
    }

    @Test
    public void testReplyToComment() {
        // Mock request
        ReplyRequest request = new ReplyRequest();
        request.setRootCommentId(UUID.randomUUID().toString());
        request.setUserId(UUID.randomUUID().toString());
        request.setContent("This is a reply");

        // Mock response
        CommentResponse response = new CommentResponse();
        response.setContent("This is a reply");

        when(commentService.replyToComment(any(ReplyRequest.class))).thenReturn(response);

        // Call controller method
        ResponseEntity<CommentResponse> result = commentController.replyToComment(request);

        // Verify result
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("This is a reply", result.getBody().getContent());
    }

    @Test
    public void testGetCommentsByCourseId() {
        String courseId = UUID.randomUUID().toString();

        // Mock response
        CommentResponse response = new CommentResponse();
        response.setContent("Test comment");
        List<CommentResponse> comments = Collections.singletonList(response);

        when(commentService.getCommentsByCourseId(anyString())).thenReturn(comments);

        // Call controller method
        ResponseEntity<List<CommentResponse>> result = (ResponseEntity<List<CommentResponse>>) commentController.getCommentsByCourseId(courseId);

        // Verify result
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals("Test comment", result.getBody().get(0).getContent());
    }

    @Test
    public void testUpdateCommentContent() {
        UUID commentId = UUID.randomUUID();
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setContent("Updated content");

        // Mock response
        CommentResponse response = new CommentResponse();
        response.setContent("Updated content");

        when(commentService.updateCommentContent(any(UUID.class), any(UpdateCommentRequest.class))).thenReturn(response);

        // Call controller method
        ResponseEntity<CommentResponse> result = (ResponseEntity<CommentResponse>) commentController.updateComment(commentId.toString(), request);

        // Verify result
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Updated content", result.getBody().getContent());
    }

    @Test
    public void testDeleteComment() {
        UUID commentId = UUID.randomUUID();

        when(commentService.deleteComment(any(UUID.class))).thenReturn("Comment deleted");

        // Call controller method
        ResponseEntity<?> result = commentController.deleteComment(commentId.toString());

        // Verify result
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Comment deleted", result.getBody());
    }
}
