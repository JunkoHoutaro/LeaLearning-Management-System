package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, String> {

    // Lấy danh sách Lesson theo Chapter
    List<Lesson> findByChapter(Chapter chapter);

    // Kiểm tra tồn tại Lesson theo tên và Chapter
    boolean existsByNameAndChapter(String name, Chapter chapter);
    List<Lesson> findByChapterId(String id);
    List<Lesson> findByChapterIdAndIsDemo(String chapterId, int isDemo);
    boolean existsByChapterIdAndIndex(String chapterId, int index);
}
