package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Comment;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    private User user;
    private Comment comment;
    private Course course;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
        user = userRepository.save(user);

        // Tạo Course
        course = Course.builder()
                .name("Java Programming")
                .price(200f)
                .build();
        course = courseRepository.save(course);

        // Tạo Comment và liên kết với User và Course
        comment = Comment.builder()
                .user(user)
                .content("This is a test comment")
                .course(course)
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();
        comment = commentRepository.save(comment);
    }

    @AfterEach
    void tearDown() {
        commentRepository.deleteAll();
        userRepository.deleteAll();
        courseRepository.deleteAll();
    }

    @Test
    void testSaveComment() {
        Comment mockComment = Comment.builder()
                .user(user)
                .content("This is another test comment")
                .course(course)
                .build();

        Comment savedComment = commentRepository.save(mockComment);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getId()).isNotNull();
        assertThat(savedComment.getContent()).isEqualTo("This is another test comment");
    }

    // Test tìm kiếm comment theo courseId
    @Test
    void testFindCommentsByCourseId() {
        // Truy vấn tìm comment theo courseId
        List<Comment> foundComments = commentRepository.findByCourse_Id(course.getId());

        assertThat(foundComments).isNotNull();
        assertThat(foundComments.size()).isGreaterThan(0);
        assertThat(foundComments.get(0).getCourse().getId()).isEqualTo(course.getId());
    }

    // Test tìm kiếm comment theo rootCommentId
    @Test
    void testFindCommentsByRootComment() {
        // Tạo comment gốc
        Comment rootComment = Comment.builder()
                .content("This is the root comment")
                .course(course)
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();
        rootComment = commentRepository.save(rootComment);

        // Tạo comment trả lời
        Comment replyComment = Comment.builder()
                .content("This is a reply comment")
                .course(course)
                .rootComment(rootComment.getId())  // Liên kết với rootCommentId
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();
        replyComment = commentRepository.save(replyComment);

        // Truy vấn comment theo rootCommentId
        List<Comment> foundComments = commentRepository.findByRootComment(rootComment.getId());

        // Kiểm tra kết quả
        assertThat(foundComments).isNotNull();
        assertThat(foundComments.size()).isGreaterThan(0);
        assertThat(foundComments.get(0).getRootComment()).isEqualTo(rootComment.getId());
    }

    // Test tìm comment theo ID
    @Test
    void testFindCommentById() {
        Optional<Comment> foundComment = commentRepository.findById(comment.getId());

        assertThat(foundComment).isPresent();
        assertThat(foundComment.get().getContent()).isEqualTo("This is a test comment");
    }

    // Test xoá comment
    @Test
    void testDeleteComment() {
        commentRepository.delete(comment);

        Optional<Comment> deletedComment = commentRepository.findById(comment.getId());

        assertThat(deletedComment).isNotPresent();
    }
}
