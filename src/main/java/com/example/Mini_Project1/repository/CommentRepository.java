package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByCourse_Id(String courseId);

    List<Comment> findByRootComment(String rootCommentId);
}
