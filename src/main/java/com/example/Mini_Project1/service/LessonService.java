package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Lesson;
import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.exception.BadRequestException;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.repository.LessonRepository;
import com.example.Mini_Project1.repository.ChapterRepository;
import com.example.Mini_Project1.request.lesson.CreateLessonRequest;
import com.example.Mini_Project1.request.lesson.UpdateLessonRequest;
import com.example.Mini_Project1.response.file.FileResponse;
import com.example.Mini_Project1.response.lesson.LessonResponse;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.modelmapper.TypeToken;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ChapterRepository chapterRepository;
    private final FileService fileService;

    public LessonService(LessonRepository lessonRepository, ChapterRepository chapterRepository, FileService fileService) {
        this.lessonRepository = lessonRepository;
        this.chapterRepository = chapterRepository;
        this.fileService = fileService;
    }

    @Transactional
    public LessonResponse createLesson(CreateLessonRequest request) {
        Chapter chapter = chapterRepository.findById(request.getChapterId().toString()).orElseThrow(
                () -> new RuntimeException("Chapter not found with ID: " + request.getChapterId().toString()));

        if(lessonRepository.existsByChapterIdAndIndex(request.getChapterId().toString(), request.getIndex()))
            throw new BadRequestException("This chapter already has lesson with index " + request.getIndex());

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        Lesson lesson = modelMapper.map(request, Lesson.class);
        lesson.setChapter(chapter);
        lesson.setCreatedDate(new Date());
        lesson.setUpdatedDate(new Date());

        Lesson savedLesson = lessonRepository.save(lesson);
        return modelMapper.map(savedLesson, LessonResponse.class);
    }

    public List<LessonResponse> getLessonsByChapter(UUID chapterId) {

        Chapter chapter = chapterRepository.findById(chapterId.toString()).orElseThrow(
                () -> new RuntimeException("Chapter not found with ID: " + chapterId.toString()));

        List<Lesson> lessons = lessonRepository.findByChapter(chapter);
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(lessons, new TypeToken<List<LessonResponse>>() {
        }.getType());
    }

    @Transactional
    public LessonResponse updateLesson(UpdateLessonRequest request) {

        Lesson lesson = lessonRepository.findById(request.getLessonId().toString()).orElseThrow(
                () -> new RuntimeException("Lesson not found with ID: " + request.getLessonId().toString()));

        if(request.getName() != null && request.getName().isBlank())
            throw new BadRequestException("Lesson's name cannot be empty");

        Hibernate.initialize(lesson.getChapter());
        if(lessonRepository.existsByChapterIdAndIndex(lesson.getChapter().getId(), request.getIndex()))
            throw new BadRequestException("This chapter already has lesson with index " + request.getIndex());

        lesson.setUpdatedDate(new Date());

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(request, lesson);

        Lesson updatedLesson = lessonRepository.save(lesson);
        return modelMapper.map(updatedLesson, LessonResponse.class);
    }

    @Transactional
    public LessonResponse deleteLesson(UUID lessonId) {
        // Kiểm tra nếu Lesson có tồn tại không
        Lesson lesson = lessonRepository.findById(lessonId.toString())
                .orElseThrow(() -> new RuntimeException("Lesson not found with ID: " + lessonId.toString()));

        // Map Lesson entity to LessonResponse before deleting
        LessonResponse lessonResponse = new ModelMapper().map(lesson, LessonResponse.class);

        // Xóa lesson
        lessonRepository.delete(lesson);

        // Trả về LessonResponse sau khi xóa
        return lessonResponse;
    }

    public FileResponse uploadLessonVideo(UUID lessonId, MultipartFile file) throws Exception {
        String contentType = file.getContentType();

        if(contentType == null|| !contentType.startsWith("video/")) {
            throw new BadRequestException("This file is not a video file.");
        }

        Lesson lesson = lessonRepository.findById(lessonId.toString()).orElseThrow(
                ()-> new NotFoundException("Can't find lesson with id: " + lessonId)
        );

        if(lesson.getVideo_url() != null){
            fileService.removeResource(lesson.getVideo_url());
        }

        FileResponse fileResponse = fileService.uploadResource(lessonId.toString(), file);
        lesson.setVideo_url(fileResponse.getUrl());
        lessonRepository.save(lesson);

        return fileResponse;
    }

    public FileResponse uploadLessonResource(UUID lessonId, MultipartFile file) throws Exception {
        Lesson lesson = lessonRepository.findById(lessonId.toString()).orElseThrow(
                ()-> new NotFoundException("Can't find lesson with id: " + lessonId)
        );

        if(lesson.getResource_url() != null){
            fileService.removeResource(lesson.getResource_url());
        }

        FileResponse fileResponse = fileService.uploadResource(lessonId.toString(), file);
        lesson.setResource_url(fileResponse.getUrl());
        lessonRepository.save(lesson);

        return fileResponse;
    }
}
