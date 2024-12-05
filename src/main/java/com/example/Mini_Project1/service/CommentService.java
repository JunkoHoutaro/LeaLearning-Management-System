package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Comment;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.repository.CommentRepository;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.repository.UserRepository;
import com.example.Mini_Project1.request.CommentRequest;
import com.example.Mini_Project1.request.ReplyRequest;
import com.example.Mini_Project1.response.CommentResponse;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CommentResponse createComment(CommentRequest request) {
        // Kiểm tra nếu courseId, userId, hoặc content là null
        if (request.getCourseId() == null || request.getUserId() == null || request.getContent() == null) {
            throw new IllegalArgumentException("Course, User, or Content cannot be null");
        }

        // Chuyển đổi từ String sang UUID, với điều kiện là chúng không null
        UUID courseId = null;
        UUID userId = null;

        try {
            courseId = UUID.fromString(request.getCourseId());
            if (request.getUserId() != null) {
                userId = UUID.fromString(request.getUserId());
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for CourseId or UserId");
        }

        // Tìm course và user từ cơ sở dữ liệu
        Course course = courseRepository.findById(courseId.toString())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        assert userId != null;
        User user = userRepository.findById(userId.toString())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo mới comment
        Comment newComment = Comment.builder()
                .course(course)
                .user(user)
                .content(request.getContent())
                .rootComment(null)
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();

        // Lưu comment vào cơ sở dữ liệu
        commentRepository.save(newComment);

        // Trả về CommentResponse
        return modelMapper.map(newComment, CommentResponse.class);
    }
    // Trả lời comment
    public CommentResponse replyToComment(ReplyRequest request) {
        // Tìm comment gốc và người dùng từ cơ sở dữ liệu
        Comment parentComment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        User user = userRepository.findById(request.getUserId().toString())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo mới comment trả lời
        Comment reply = Comment.builder()
                .course(parentComment.getCourse())
                .user(user)
                .content(request.getContent())
                .rootComment(request.getCommentId().toString()) // Lưu ID comment gốc để trả lời
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();

        // Lưu comment trả lời vào cơ sở dữ liệu
        commentRepository.save(reply);

        // Trả về CommentResponse sử dụng ModelMapper
        return modelMapper.map(reply, CommentResponse.class);
    }

    // Lấy danh sách comment của một khóa học
    public List<CommentResponse> getCommentsByCourseId(String courseId) {
        List<Comment> comments = commentRepository.findByCourse_Id(courseId);
        return comments.stream()
                .map(comment -> modelMapper.map(comment, CommentResponse.class)) // Dùng ModelMapper để ánh xạ
                .collect(Collectors.toList());
    }

    // Lấy danh sách tất cả comment
    public List<CommentResponse> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        return comments.stream()
                .map(comment -> modelMapper.map(comment, CommentResponse.class)) // Dùng ModelMapper để ánh xạ
                .collect(Collectors.toList());
    }

    // Cập nhật comment
    public CommentResponse updateComment(UUID commentId, CommentRequest request) {
        // Tìm comment từ cơ sở dữ liệu
        Comment comment = commentRepository.findById(commentId.toString())
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Cập nhật nội dung comment và thời gian cập nhật
        comment.setContent(request.getContent());
        comment.setUpdatedDate(new Date());

        // Lưu lại comment đã cập nhật vào cơ sở dữ liệu
        commentRepository.save(comment);

        // Trả về CommentResponse sử dụng ModelMapper
        return modelMapper.map(comment, CommentResponse.class);
    }

    // Xóa comment
    public String deleteComment(UUID commentId) {
        // Tìm và xóa comment từ cơ sở dữ liệu
        Comment comment = commentRepository.findById(commentId.toString())
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        commentRepository.delete(comment);
        return "Comment deleted successfully";
    }
}
