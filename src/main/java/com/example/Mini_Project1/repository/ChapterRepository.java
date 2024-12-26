package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.entity.Course;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterRepository extends JpaRepository<Chapter, String> {

    List<Chapter> findByCourse(Course course);

    boolean existsByNameAndCourse(String name, Course course);

    List<Chapter> getChapterByCourseId(String courseId);

    boolean existsByCourseIdAndIndex(String courseId, int index);
}
