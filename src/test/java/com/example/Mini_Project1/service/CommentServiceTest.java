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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CommentServiceTest {

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

    private UUID courseId;
    private UUID userId;
    private UUID commentId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        courseId = UUID.randomUUID();
        userId = UUID.randomUUID();
        commentId = UUID.randomUUID();
    }

    @Test
    public void testCreateComment_Success() {
        CommentRequest request = new CommentRequest();
        request.setCourseId(courseId.toString());
        request.setUserId(userId.toString());
        request.setContent("Test comment");

        Course course = new Course();
        course.setId(courseId.toString());
        User user = new User();
        user.setId(userId.toString());

        Comment newComment = Comment.builder()
                .course(course)
                .user(user)
                .content(request.getContent())
                .rootComment(null)
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();

        when(courseRepository.findById(courseId.toString())).thenReturn(Optional.of(course));
        when(userRepository.findById(userId.toString())).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(newComment);
        when(modelMapper.map(any(Comment.class), eq(CommentResponse.class))).thenReturn(new CommentResponse());

        CommentResponse response = commentService.createComment(request);

        assertNotNull(response);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void testCreateComment_InvalidUUID() {
        CommentRequest request = new CommentRequest();
        request.setCourseId("invalid-uuid");
        request.setUserId(userId.toString());
        request.setContent("Test comment");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            commentService.createComment(request);
        });

        assertEquals("Invalid UUID format for CourseId or UserId", exception.getMessage());
    }

    @Test
    public void testReplyToComment_Success() {
        ReplyRequest request = new ReplyRequest();
        request.setRootCommentId(commentId.toString());
        request.setUserId(userId.toString());
        request.setContent("Reply to comment");

        Comment parentComment = new Comment();
        parentComment.setId(commentId.toString());

        User user = new User();
        user.setId(userId.toString());

        Comment reply = Comment.builder()
                .course(parentComment.getCourse())
                .user(user)
                .content(request.getContent())
                .rootComment(request.getRootCommentId())
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();

        when(commentRepository.findById(commentId.toString())).thenReturn(Optional.of(parentComment));
        when(userRepository.findById(userId.toString())).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(reply);
        when(modelMapper.map(any(Comment.class), eq(CommentResponse.class))).thenReturn(new CommentResponse());

        CommentResponse response = commentService.replyToComment(request);

        assertNotNull(response);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void testReplyToComment_ParentNotFound() {
        ReplyRequest request = new ReplyRequest();
        request.setRootCommentId(commentId.toString());
        request.setUserId(userId.toString());
        request.setContent("Reply to comment");

        when(commentRepository.findById(commentId.toString())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            commentService.replyToComment(request);
        });

        assertEquals("Parent comment not found", exception.getMessage());
    }

    @Test
    public void testGetCommentsByCourseId_Success() {
        List<Comment> comments = Arrays.asList(new Comment(), new Comment());
        when(commentRepository.findByCourse_Id(courseId.toString())).thenReturn(comments);
        when(modelMapper.map(any(Comment.class), eq(CommentResponse.class)))
                .thenReturn(new CommentResponse());

        List<CommentResponse> response = commentService.getCommentsByCourseId(courseId.toString());

        assertEquals(2, response.size());
        verify(commentRepository, times(1)).findByCourse_Id(courseId.toString());
    }

    @Test
    public void testUpdateComment_Success() {
        // Tạo yêu cầu cập nhật comment
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setContent("Updated comment");

        // Tạo comment hiện tại (comment cũ)
        Comment existingComment = new Comment();
        existingComment.setId(commentId.toString());
        existingComment.setContent("Old comment");

        // Tạo comment sau khi được cập nhật
        Comment updatedComment = new Comment();
        updatedComment.setId(commentId.toString());
        updatedComment.setContent(request.getContent()); // Nội dung được cập nhật

        // Tạo CommentResponse với nội dung mới
        CommentResponse updatedCommentResponse = new CommentResponse();
        updatedCommentResponse.setContent(request.getContent()); // Đảm bảo giá trị content được cập nhật

        // Mocks behavior của commentRepository và modelMapper
        when(commentRepository.findById(commentId.toString())).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(updatedComment); // Mocks save()
        when(modelMapper.map(any(Comment.class), eq(CommentResponse.class))).thenReturn(updatedCommentResponse); // Mocks modelMapper

        // Gọi phương thức updateCommentContent
        CommentResponse response = commentService.updateCommentContent(commentId, request);

        // Kiểm tra kết quả
        assertNotNull(response);
        assertEquals("Updated comment", response.getContent()); // Kiểm tra nội dung đã được cập nhật
        verify(commentRepository, times(1)).save(any(Comment.class)); // Kiểm tra save được gọi đúng một lần
    }


    @Test
    public void testDeleteComment_Success() {
        Comment comment = new Comment();
        comment.setId(commentId.toString());

        when(commentRepository.findById(commentId.toString())).thenReturn(Optional.of(comment));

        String result = commentService.deleteComment(commentId);

        assertEquals("Comment and its replies deleted successfully", result);
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    public void testDeleteComment_CommentNotFound() {
        when(commentRepository.findById(commentId.toString())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            commentService.deleteComment(commentId);
        });

        assertEquals("Comment not found", exception.getMessage());
    }
}
