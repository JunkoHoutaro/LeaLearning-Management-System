//package com.example.Mini_Project1.controller;
//
//import com.example.Mini_Project1.request.course.CommentRequest;
//import com.example.Mini_Project1.request.course.ReplyRequest;
//import com.example.Mini_Project1.request.course.UpdateCommentRequest;
//import com.example.Mini_Project1.response.course.CommentResponse;
//import com.example.Mini_Project1.service.CommentService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//public class CommentControllerTest {
//
//    @Mock
//    private CommentService commentService;
//
//    @InjectMocks
//    private CommentController commentController;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testCreateComment() {
//        CommentRequest request = new CommentRequest();
//        request.setContent("This is a test comment");
//        request.setCourseId(UUID.fromString(UUID.randomUUID().toString()));
//        request.setUserId(UUID.randomUUID().toString());
//
//        CommentResponse response = new CommentResponse();
//        response.setContent("This is a test comment");
//
//        when(commentService.createComment(any(CommentRequest.class))).thenReturn(response);
//
//        ResponseEntity<CommentResponse> result = commentController.createComment(request);
//
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertEquals("This is a test comment", result.getBody().getContent());
//    }
//
//    @Test
//    public void testReplyToComment() {
//        ReplyRequest replyRequest = new ReplyRequest();
//        replyRequest.setContent("This is a reply");
//        replyRequest.setRootCommentId(UUID.randomUUID().toString());
//        replyRequest.setUserId(UUID.randomUUID().toString());
//
//        CommentResponse response = new CommentResponse();
//        response.setContent("This is a reply");
//
//        when(commentService.replyToComment(any(ReplyRequest.class))).thenReturn(response);
//
//        ResponseEntity<CommentResponse> result = commentController.replyToComment(replyRequest);
//
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertEquals("This is a reply", result.getBody().getContent());
//    }
//
//    @Test
//    public void testGetAllComments() {
//        CommentResponse response = new CommentResponse();
//        response.setContent("This is a test comment");
//
//        when(commentService.getAllComments()).thenReturn(Collections.singletonList(response));
//
//        ResponseEntity<List<CommentResponse>> result = commentController.getAllComments();
//
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertEquals(1, result.getBody().size());
//    }
//
//    @Test
//    public void testGetCommentsByCourseId() {
//        String courseId = UUID.randomUUID().toString();
//
//        CommentResponse response = new CommentResponse();
//        response.setContent("This is a test comment");
//
//        when(commentService.getCommentsByCourseId(courseId)).thenReturn(Collections.singletonList(response));
//
//        ResponseEntity<List<CommentResponse>> result = (ResponseEntity<List<CommentResponse>>) commentController.getCommentsByCourseId(courseId);
//
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertEquals(1, result.getBody().size());
//    }
//
//    @Test
//    public void testGetCommentsByCourseIdInvalidUUID() {
//        String invalidCourseId = "invalid-uuid";
//
//        ResponseEntity<?> result = commentController.getCommentsByCourseId(invalidCourseId);
//
//        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
//        assertEquals("Invalid UUID format for courseId", result.getBody());
//    }
//
//    @Test
//    public void testUpdateComment() {
//        String commentId = UUID.randomUUID().toString();
//        UpdateCommentRequest updateRequest = new UpdateCommentRequest();
//        updateRequest.setContent("Updated content");
//
//        CommentResponse response = new CommentResponse();
//        response.setContent("Updated content");
//
//        when(commentService.updateCommentContent(any(UUID.class), any(UpdateCommentRequest.class))).thenReturn(response);
//
//        ResponseEntity<?> result = commentController.updateComment(commentId, updateRequest);
//
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertEquals("Updated content", ((CommentResponse) result.getBody()).getContent());
//    }
//
//    @Test
//    public void testUpdateCommentInvalidUUID() {
//        String invalidCommentId = "invalid-uuid";
//        UpdateCommentRequest updateRequest = new UpdateCommentRequest();
//        updateRequest.setContent("Updated content");
//
//        ResponseEntity<?> result = commentController.updateComment(invalidCommentId, updateRequest);
//
//        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
//        assertEquals("Invalid UUID format for commentId", result.getBody());
//    }
//
//    @Test
//    public void testDeleteComment() {
//        String commentId = UUID.randomUUID().toString();
//        String message = "Comment deleted successfully";
//
//        when(commentService.deleteComment(any(UUID.class))).thenReturn(message);
//
//        ResponseEntity<?> result = commentController.deleteComment(commentId);
//
//        assertEquals(HttpStatus.OK, result.getStatusCode());
//        assertEquals(message, result.getBody());
//    }
//
//    @Test
//    public void testDeleteCommentInvalidUUID() {
//        String invalidCommentId = "invalid-uuid";
//
//        ResponseEntity<?> result = commentController.deleteComment(invalidCommentId);
//
//        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
//        assertEquals("Invalid UUID format for commentId", result.getBody());
//    }
//}
