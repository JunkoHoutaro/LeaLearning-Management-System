package com.example.Mini_Project1.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Lesson;
import com.example.Mini_Project1.exception.AccessDeniedException;
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

@Service
@AllArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;
    private final LessonRepository lessonRepository;

    @Transactional
    public ChapterResponse createChapter(CreateChapterRequest request, UserDetails userDetails) {
        Course course = courseRepository.findById(request.getCourseId().toString())
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + request.getCourseId().toString()));

        if (!course.getUser().getId().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You do not have permission to create a chapter for this course.");
        }

        if (request.getIndex() <= 0) {
            throw new BadRequestException("Chapter index must be greater than 0");
        }

        if (chapterRepository.existsByCourseIdAndIndex(request.getCourseId().toString(), request.getIndex())) {
            throw new BadRequestException("This course already has a chapter with index " + request.getIndex());
        }

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
    public ChapterResponse updateChapter(UpdateChapterRequest request, UserDetails userDetails) {
        Chapter chapter = chapterRepository.findById(request.getChapterId().toString())
                .orElseThrow(() -> new ChapterNotFoundException("Chapter not found with ID: " + request.getChapterId().toString()));
        Course course = chapter.getCourse();

        if (!course.getUser().getId().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You do not have permission to update this chapter.");
        }

        if (request.getName() != null && request.getName().isBlank()) {
            throw new BadRequestException("Name cannot be empty");
        }

        if (chapterRepository.existsByCourseIdAndIndex(course.getId(), request.getIndex())) {
            throw new BadRequestException("This course already has a chapter with index " + request.getIndex());
        }

        modelMapper.map(request, chapter);
        chapter.setUpdatedDate(new Date());

        Chapter updatedChapter = chapterRepository.save(chapter);
        return modelMapper.map(updatedChapter, ChapterResponse.class);
    }

    @Transactional
    public ChapterResponse deleteChapter(UUID chapterId, UserDetails userDetails) {
        Chapter chapter = chapterRepository.findById(chapterId.toString())
                .orElseThrow(() -> new ChapterNotFoundException("Chapter not found with ID: " + chapterId.toString()));
        Course course = chapter.getCourse();

        if (!course.getUser().getId().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You do not have permission to delete this chapter.");
        }

        chapterRepository.delete(chapter);
        return modelMapper.map(chapter, ChapterResponse.class);
    }

    @Transactional
    public ChapterDetailsResponse getChapterDetails(UUID chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId.toString())
                .orElseThrow(() -> new ChapterNotFoundException("Chapter not found with ID: " + chapterId.toString()));
        List<Lesson> lessons = lessonRepository.findByChapter(chapter);
        List<LessonResponse> lessonResponses = modelMapper.map(lessons, new TypeToken<List<LessonResponse>>() {
        }.getType());

        ChapterDetailsResponse chapterDetailsResponse = new ChapterDetailsResponse();
        chapterDetailsResponse.setChapter(modelMapper.map(chapter, ChapterResponse.class));
        chapterDetailsResponse.setLessons(lessonResponses);

        return chapterDetailsResponse;
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
