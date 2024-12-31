package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Comment;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.entity.Payment;
import com.example.Mini_Project1.repository.CommentRepository;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.repository.UserRepository;
import com.example.Mini_Project1.repository.PaymentRepository;
import com.example.Mini_Project1.request.course.CommentRequest;
import com.example.Mini_Project1.request.course.ReplyRequest;
import com.example.Mini_Project1.request.course.UpdateCommentRequest;
import com.example.Mini_Project1.response.course.CommentResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;

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
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;

    // Tạo comment mới
    public CommentResponse createComment(CommentRequest request) {
        // Kiểm tra nếu content là null
        if (request.getContent() == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }

        UUID courseId = null;
        try {
            courseId = UUID.fromString(String.valueOf(request.getCourseId()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for CourseId");
        }

        // Lấy user từ
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userDetails.getUsername())  // userDetails.getUsername() là userId trong JWT
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra sự tồn tại của userId và courseId trong bảng Payment
        Payment payment = paymentRepository.findByUserIdAndCourseId(user.getId(), courseId.toString());
        if (payment == null) {
            throw new RuntimeException("User is not authorized to comment. Please make sure payment exists for this course.");
        }

        // Tìm khóa học từ cơ sở dữ liệu
        Course course = courseRepository.findById(courseId.toString())
                .orElseThrow(() -> new RuntimeException("Course not found"));

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
    public CommentResponse replyToComment(ReplyRequest request, UserDetails userDetails) {
        // Tìm comment cha từ cơ sở dữ liệu
        Comment parentComment = commentRepository.findById(request.getRootCommentId())
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        // Lấy user từ UserDetails
        User user = userRepository.findById(userDetails.getUsername())  // userDetails.getUsername() là userId
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra xem người dùng có thanh toán cho khóa học này không
        Payment payment = paymentRepository.findByUserIdAndCourseId(user.getId(), parentComment.getCourse().getId());
        if (payment == null) {
            throw new RuntimeException("User has not completed payment for this course");
        }

        // Tạo mới comment trả lời
        Comment reply = Comment.builder()
                .course(parentComment.getCourse())
                .user(user)
                .content(request.getContent())
                .rootComment(request.getRootCommentId())
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();

        // Lưu comment trả lời vào cơ sở dữ liệu
        commentRepository.save(reply);

        return modelMapper.map(reply, CommentResponse.class);
    }


    // Lấy danh sách tất cả comment
    public List<CommentResponse> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        return comments.stream()
                .map(comment -> modelMapper.map(comment, CommentResponse.class))
                .collect(Collectors.toList());
    }

    // Cập nhật nội dung comment
    public CommentResponse updateCommentContent(UUID commentId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId.toString())
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Cập nhật nội dung comment và thời gian sửa đổi
        comment.setContent(request.getContent());
        comment.setUpdatedDate(new Date());
        commentRepository.save(comment);

        return modelMapper.map(comment, CommentResponse.class);
    }

    // Xóa comment
    @Transactional
    public String deleteComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId.toString())
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        deleteReplies(commentId.toString());

        commentRepository.delete(comment);

        return "Comment and its replies deleted successfully";
    }

    private void deleteReplies(String rootCommentId) {
        List<Comment> replies = commentRepository.findByRootComment(rootCommentId);
        for (Comment reply : replies) {
            deleteReplies(reply.getId());
            commentRepository.delete(reply);
        }
    }

    public List<CommentResponse> getCommentsByCourseId(String courseId) {
        // Kiểm tra UUID format
        UUID courseUUID;
        try {
            courseUUID = UUID.fromString(courseId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format for CourseId");
        }

        // Lấy danh sách comment từ repository
        List<Comment> comments = commentRepository.findByCourseId(courseUUID.toString());
        return comments.stream()
                .map(comment -> modelMapper.map(comment, CommentResponse.class))
                .collect(Collectors.toList());
    }

}
