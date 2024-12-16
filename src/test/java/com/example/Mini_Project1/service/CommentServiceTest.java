package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Comment;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.repository.CommentRepository;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.repository.UserRepository;
import com.example.Mini_Project1.request.course.CommentRequest;
import com.example.Mini_Project1.request.course.ReplyRequest;
import com.example.Mini_Project1.request.course.UpdateCommentRequest;
import com.example.Mini_Project1.response.course.CommentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CommentService commentService;

    private Course course;
    private User user;
    private Comment comment;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(UUID.randomUUID().toString());

        user = new User();
        user.setId(UUID.randomUUID().toString());

        comment = new Comment();
        comment.setId(UUID.randomUUID().toString());
        comment.setCourse(course);
        comment.setUser(user);
        comment.setContent("Test comment");
        comment.setCreatedDate(new Date());
        comment.setUpdatedDate(new Date());
    }

    @Test
    void testCreateComment_ShouldReturnCommentResponse() {
        CommentRequest request = new CommentRequest();
        request.setCourseId(course.getId());
        request.setUserId(user.getId());
        request.setContent("Test comment");

        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // Sử dụng doReturn().when() thay vì when().thenReturn()
        doReturn(new CommentResponse()).when(modelMapper).map(any(Comment.class), eq(CommentResponse.class));

        CommentResponse response = commentService.createComment(request);

        assertNotNull(response);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testCreateComment_WhenCourseNotFound_ShouldThrowException() {
        CommentRequest request = new CommentRequest();
        request.setCourseId(UUID.randomUUID().toString());
        request.setUserId(user.getId());
        request.setContent("Test comment");

        when(courseRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentService.createComment(request));
    }

    @Test
    void testCreateComment_WhenUserNotFound_ShouldThrowException() {
        CommentRequest request = new CommentRequest();
        request.setCourseId(course.getId());
        request.setUserId(UUID.randomUUID().toString());
        request.setContent("Test comment");

        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentService.createComment(request));
    }

    @Test
    void testReplyToComment_ShouldReturnCommentResponse() {
        ReplyRequest request = new ReplyRequest();
        request.setRootCommentId(comment.getId());
        request.setUserId(user.getId());
        request.setContent("Test reply");

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Comment reply = new Comment();
        reply.setId(UUID.randomUUID().toString());
        reply.setCourse(course);
        reply.setUser(user);
        reply.setContent("Test reply");
        reply.setRootComment(comment.getId());
        reply.setCreatedDate(new Date());
        reply.setUpdatedDate(new Date());

        when(commentRepository.save(any(Comment.class))).thenReturn(reply);
        doReturn(new CommentResponse()).when(modelMapper).map(any(Comment.class), eq(CommentResponse.class));

        CommentResponse response = commentService.replyToComment(request);

        assertNotNull(response);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testReplyToComment_WhenParentCommentNotFound_ShouldThrowException() {
        ReplyRequest request = new ReplyRequest();
        request.setRootCommentId(UUID.randomUUID().toString());
        request.setUserId(user.getId());
        request.setContent("Test reply");

        when(commentRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentService.replyToComment(request));
    }

    @Test
    void testUpdateCommentContent_ShouldReturnUpdatedCommentResponse() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setContent("Updated content");

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        doReturn(new CommentResponse()).when(modelMapper).map(any(Comment.class), eq(CommentResponse.class));

        CommentResponse response = commentService.updateCommentContent(UUID.fromString(comment.getId()), request);

        assertNotNull(response);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testUpdateCommentContent_WhenCommentNotFound_ShouldThrowException() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setContent("Updated content");

        when(commentRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentService.updateCommentContent(UUID.randomUUID(), request));
    }

    @Test
    void testDeleteComment_ShouldReturnSuccessMessage() {
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        String result = commentService.deleteComment(UUID.fromString(comment.getId()));

        assertEquals("Comment and its replies deleted successfully", result);
        verify(commentRepository).delete(any(Comment.class));
    }

    @Test
    void testDeleteComment_WhenCommentNotFound_ShouldThrowException() {
        when(commentRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentService.deleteComment(UUID.randomUUID()));
    }
}
