package com.example.Mini_Project1.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Lesson;
import com.example.Mini_Project1.exception.AccessDeniedException;
import com.example.Mini_Project1.exception.BadRequestException;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.repository.ChapterRepository;
import com.example.Mini_Project1.repository.LessonRepository;
import com.example.Mini_Project1.request.lesson.CreateLessonRequest;
import com.example.Mini_Project1.request.lesson.UpdateLessonRequest;
import com.example.Mini_Project1.response.file.FileResponse;
import com.example.Mini_Project1.response.lesson.LessonResponse;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ChapterRepository chapterRepository;
    private final FileService fileService;
    private final ModelMapper modelMapper;

    @Transactional
    public LessonResponse createLesson(CreateLessonRequest request, UserDetails userDetails) throws Exception {
        Chapter chapter = chapterRepository.findById(request.getChapterId().toString()).orElseThrow(
                () -> new NotFoundException("Chapter not found with ID: " + request.getChapterId().toString()));

        if (lessonRepository.existsByChapterIdAndIndex(request.getChapterId().toString(), request.getIndex())) {
            throw new BadRequestException("This chapter already has a lesson with index " + request.getIndex());
        }

        Lesson lesson = modelMapper.map(request, Lesson.class);
        lesson.setChapter(chapter);
        lesson.setCreatedDate(new Date());
        lesson.setUpdatedDate(new Date());

        Lesson savedLesson = lessonRepository.save(lesson);
        return modelMapper.map(savedLesson, LessonResponse.class);
    }

    public List<LessonResponse> getLessonsByChapter(UUID chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId.toString()).orElseThrow(
                () -> new NotFoundException("Chapter not found with ID: " + chapterId.toString()));
        List<Lesson> lessons = lessonRepository.findByChapter(chapter);
        return modelMapper.map(lessons, new TypeToken<List<LessonResponse>>() {
        }.getType());
    }

    @Transactional
    public LessonResponse updateLesson(UpdateLessonRequest request, UserDetails userDetails) {
        Lesson lesson = lessonRepository.findById(request.getLessonId().toString()).orElseThrow(
                () -> new NotFoundException("Lesson not found with ID: " + request.getLessonId().toString()));
        Chapter chapter = lesson.getChapter();
        Course course = chapter.getCourse();

        if (!course.getUser().getId().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You do not have permission to update this lesson.");
        }

        if (request.getName() != null && request.getName().isBlank()) {
            throw new BadRequestException("Lesson's name cannot be empty");
        }

        Hibernate.initialize(lesson.getChapter());
        if (lessonRepository.existsByChapterIdAndIndex(lesson.getChapter().getId(), request.getIndex())) {
            throw new BadRequestException("This chapter already has lesson with index " + request.getIndex());
        }

        lesson.setUpdatedDate(new Date());

        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(request, lesson);

        Lesson updatedLesson = lessonRepository.save(lesson);
        return modelMapper.map(updatedLesson, LessonResponse.class);
    }

    @Transactional
    public LessonResponse deleteLesson(UUID lessonId, UserDetails userDetails) {
        Lesson lesson = lessonRepository.findById(lessonId.toString())
                .orElseThrow(() -> new NotFoundException("Lesson not found with ID: " + lessonId.toString()));
        Chapter chapter = lesson.getChapter();
        Course course = chapter.getCourse();

        if (!course.getUser().getId().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You do not have permission to delete this lesson.");
        }

        lessonRepository.delete(lesson);
        return modelMapper.map(lesson, LessonResponse.class);
    }

    public FileResponse uploadLessonVideo(UUID lessonId, MultipartFile file) throws Exception {
        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("video/")) {
            throw new BadRequestException("This file is not a video file.");
        }

        Lesson lesson = lessonRepository.findById(lessonId.toString()).orElseThrow(
                () -> new NotFoundException("Can't find lesson with id: " + lessonId));

        if (lesson.getVideo_url() != null) {
            fileService.removeResource(lesson.getVideo_url());
        }

        FileResponse fileResponse = fileService.uploadResource(lessonId.toString(), file);
        lesson.setVideo_url(fileResponse.getUrl());
        lessonRepository.save(lesson);

        return fileResponse;
    }

    public FileResponse uploadLessonResource(UUID lessonId, MultipartFile file) throws Exception {
        Lesson lesson = lessonRepository.findById(lessonId.toString()).orElseThrow(
                () -> new NotFoundException("Can't find lesson with id: " + lessonId));

        if (lesson.getResource_url() != null) {
            fileService.removeResource(lesson.getResource_url());
        }

        FileResponse fileResponse = fileService.uploadResource(lessonId.toString(), file);
        lesson.setResource_url(fileResponse.getUrl());
        lessonRepository.save(lesson);

        return fileResponse;
    }
}
