package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Quizz;
import com.example.Mini_Project1.repository.ChapterRepository;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.repository.QuizzRepository;
import com.example.Mini_Project1.request.QuizzAndQuestion.*;
import com.example.Mini_Project1.response.QuizzAndQuestion.QuizzResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;

@Service
@AllArgsConstructor
public class QuizzService {

    private final QuizzRepository quizzRepository;
    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;

    @Transactional
    // create quizz by course id
    public QuizzResponse createQuizzByCourseService(CreateQuizzByCourseRequest request) {
        Course course = courseRepository.findById(request.getCourseId().toString()).orElseThrow(
                () -> new RuntimeException("Course not found with id " + request.getCourseId().toString()));
        List<Quizz> quizzByCourse = quizzRepository.findByCourse(course);

        boolean isNameDuplicate = quizzByCourse.stream()
                .anyMatch(quizz -> quizz.getName().equals(request.getName()));

        if (isNameDuplicate)
            throw new RuntimeException("Quizz already exists in this course.");

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        Quizz quizz = modelMapper.map(request, Quizz.class);
        quizz.setCreatedDate(new Date());
        quizz.setUpdatedDate(new Date());

        return modelMapper.map(quizzRepository.save(quizz), QuizzResponse.class);
    }

    @Transactional
    // create quizz by chapter id
    public QuizzResponse createQuizzByChapterService(CreateQuizzByChapterRequest request) {
        Chapter chapter = chapterRepository.findById(request.getChapterId().toString()).orElseThrow(
                () -> new RuntimeException("Chapter not find with id " + request.getChapterId().toString()));
        List<Quizz> quizzByChapter = quizzRepository.findByChapter(chapter);
        Course couseByChapter = chapter.getCourse();

        boolean isNameDuplicate = quizzByChapter.stream()
                .anyMatch(quizz -> quizz.getName().equals(request.getName()));

        if (isNameDuplicate)
            throw new RuntimeException("Quizz already exists in this chapter.");

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        Quizz quizz = modelMapper.map(request, Quizz.class);
        quizz.setCourse(couseByChapter);
        quizz.setCreatedDate(new Date());
        quizz.setUpdatedDate(new Date());

        return modelMapper.map(quizzRepository.save(quizz), QuizzResponse.class);
    }

    @Transactional
    // search by course
    public List<QuizzResponse> getQuizzByCourseService(UUID courseId) {
        Course course = courseRepository.findById(courseId.toString())
                .orElseThrow(() -> new RuntimeException("Course not found with id " + courseId.toString()));
        List<Quizz> quizzes = quizzRepository.findByCourse(course);

        return new ModelMapper().map(quizzes, new TypeToken<List<QuizzResponse>>() {
        }.getType());
    }

    @Transactional
    // search by chapter
    public List<QuizzResponse> getQuizzByChapterService(UUID chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId.toString())
                .orElseThrow(() -> new RuntimeException("Chapter not found with id " + chapterId.toString()));
        List<Quizz> quizzes = quizzRepository.findByChapter(chapter);

        return new ModelMapper().map(quizzes, new TypeToken<List<QuizzResponse>>() {
        }.getType());
    }

    @Transactional
    // update quizz
    public QuizzResponse updateQuizzService(UpdateQuizzRequest request) {
        Quizz quizz = quizzRepository.findById(request.getQuizzId().toString())
                .orElseThrow(() -> new RuntimeException("Course not found with id " + request.getQuizzId().toString()));

        quizz.setUpdatedDate(new Date());

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(request, quizz);

        return modelMapper.map(quizzRepository.save(quizz), QuizzResponse.class);
    }

    @Transactional
    public QuizzResponse deleteQuizzService(UUID quizzId) {
        Quizz quizz = quizzRepository.findById(quizzId.toString()).orElseThrow(
                () -> new RuntimeException("Quizz not found with id " + quizzId.toString()));

        quizzRepository.delete(quizz);
        return new ModelMapper().map(quizz, QuizzResponse.class);
    }
}
