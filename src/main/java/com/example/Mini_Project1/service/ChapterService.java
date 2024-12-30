package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Lesson;
import com.example.Mini_Project1.exception.BadRequestException;
import com.example.Mini_Project1.exception.ChapterNotFoundException;
import com.example.Mini_Project1.exception.CourseNotFoundException;
import com.example.Mini_Project1.repository.ChapterRepository;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.repository.LessonRepository;
import com.example.Mini_Project1.request.chapter.CreateChapterRequest;
import com.example.Mini_Project1.request.chapter.UpdateChapterRequest;
import com.example.Mini_Project1.response.chapter.ChapterDetailsResponse;
import com.example.Mini_Project1.response.chapter.ChapterResponse;
import com.example.Mini_Project1.response.lesson.LessonResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;
    private final LessonRepository lessonRepository;

    @Transactional
    public ChapterResponse createChapter(CreateChapterRequest request) {
        Course course = courseRepository.findById(request.getCourseId().toString())
                .orElseThrow(
                        () -> new CourseNotFoundException(
                                "Course not found with ID: " + request.getCourseId().toString()));

        if (chapterRepository.existsByCourseIdAndIndex(request.getCourseId().toString(), request.getIndex()))
            throw new BadRequestException("This course already has chapter with index " + request.getIndex());

        Chapter chapter = modelMapper.map(request, Chapter.class);

        chapter.setCourse(course);
        chapter.setCreatedDate(new Date());
        chapter.setUpdatedDate(new Date());

        Chapter savedChapter = chapterRepository.save(chapter);
        return modelMapper.map(savedChapter, ChapterResponse.class);
    }

    public List<ChapterResponse> getChaptersByCourse(UUID courseId) {

        Course course = courseRepository.findById(courseId.toString())
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId.toString()));
        List<Chapter> chapters = chapterRepository.findByCourse(course);
        return modelMapper.map(chapters, new TypeToken<List<ChapterResponse>>() {
        }.getType());
    }

    @Transactional
    public ChapterResponse updateChapter(UpdateChapterRequest request) {

        Chapter chapter = chapterRepository.findById(request.getChapterId().toString())
                .orElseThrow(
                        () -> new ChapterNotFoundException(
                                "Chapter not found with ID: " + request.getChapterId().toString()));

        if (request.getName() != null && request.getName().isBlank())
            throw new BadRequestException("Name cannot be empty");

        Hibernate.initialize(chapter.getCourse());
        if (chapterRepository.existsByCourseIdAndIndex(chapter.getCourse().getId(), request.getIndex()))
            throw new BadRequestException("This course already has chapter with index " + request.getIndex());

        modelMapper.map(request, chapter);

        chapter.setUpdatedDate(new Date());

        Chapter updatedChapter = chapterRepository.save(chapter);
        return modelMapper.map(updatedChapter, ChapterResponse.class);
    }

    @Transactional
    public ChapterResponse deleteChapter(UUID chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId.toString())
                .orElseThrow(() -> new ChapterNotFoundException("Chapter not found with ID: " + chapterId.toString()));
        ChapterResponse chapterResponse = modelMapper.map(chapter, ChapterResponse.class);
        chapterRepository.delete(chapter);
        return chapterResponse;
    }

    public List<ChapterDetailsResponse> getChapterDetails(String courseId, Boolean isPaid) {
        List<Chapter> chapters = chapterRepository.getChapterByCourseId(courseId);
        List<ChapterDetailsResponse> chapterResponses = modelMapper.map(chapters,
                new TypeToken<List<ChapterDetailsResponse>>() {
                }.getType());
        for (ChapterDetailsResponse chapterResponse : chapterResponses) {
            List<Lesson> lessons = isPaid ? lessonRepository.findByChapterId(chapterResponse.getChapter().getId())
                    : lessonRepository.findByChapterIdAndIsDemo(chapterResponse.getChapter().getId(), 1);
            List<LessonResponse> lessonResponses = modelMapper.map(lessons, new TypeToken<List<LessonResponse>>() {
            }.getType());
            chapterResponse.setLessons(lessonResponses);
        }

        return chapterResponses;
    }

}
