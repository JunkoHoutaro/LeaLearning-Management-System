package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.repository.ChapterRepository;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.request.chapter.CreateChapterRequest;
import com.example.Mini_Project1.request.chapter.UpdateChapterRequest;
import com.example.Mini_Project1.response.chapter.ChapterResponse;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;

    public ChapterService(ChapterRepository chapterRepository, CourseRepository courseRepository,
            ModelMapper modelMapper) {
        this.chapterRepository = chapterRepository;
        this.courseRepository = courseRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ChapterResponse createChapter(CreateChapterRequest request) {

        Course course = courseRepository.findById(request.getCourseId().toString())
                .orElseThrow(
                        () -> new RuntimeException("Course not found with ID: " + request.getCourseId().toString()));

        Chapter chapter = modelMapper.map(request, Chapter.class);

        chapter.setCourse(course); // Liên kết với Course
        chapter.setCreatedDate(new Date());
        chapter.setUpdatedDate(new Date());

        Chapter savedChapter = chapterRepository.save(chapter);
        return modelMapper.map(savedChapter, ChapterResponse.class);
    }

    public List<ChapterResponse> getChaptersByCourse(UUID courseId) {

        Course course = courseRepository.findById(courseId.toString())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId.toString()));
        List<Chapter> chapters = chapterRepository.findByCourse(course);
        return modelMapper.map(chapters, new TypeToken<List<ChapterResponse>>() {
        }.getType());
    }

    @Transactional
    public ChapterResponse updateChapter(UpdateChapterRequest request) {

        Chapter chapter = chapterRepository.findById(request.getChapterId().toString())
                .orElseThrow(
                        () -> new RuntimeException("Chapter not found with ID: " + request.getChapterId().toString()));

        modelMapper.map(request, chapter);

        chapter.setUpdatedDate(new Date());

        Chapter updatedChapter = chapterRepository.save(chapter);
        return modelMapper.map(updatedChapter, ChapterResponse.class);
    }

    @Transactional
    public ChapterResponse deleteChapter(UUID chapterId) {
        // Kiểm tra nếu Chapter có tồn tại không
        Chapter chapter = chapterRepository.findById(chapterId.toString())
                .orElseThrow(() -> new RuntimeException("Chapter not found with ID: " + chapterId.toString()));

        // Xóa chapter
        chapterRepository.delete(chapter);

        // Trả về ChapterResponse sau khi xóa
        return modelMapper.map(chapter, ChapterResponse.class);
    }

}
