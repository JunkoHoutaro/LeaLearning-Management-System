package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Quizz;
import com.example.Mini_Project1.exception.BadRequestException;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.repository.ChapterRepository;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.repository.QuizzRepository;
import com.example.Mini_Project1.request.QuizzAndQuestion.*;
import com.example.Mini_Project1.response.QuizzAndQuestion.QuizzResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.TypeToken;
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
    private final ModelMapper modelMapper;

    @Transactional
    // create quizz by course id
    public QuizzResponse createQuizzByCourseService(CreateQuizzByCourseRequest request) {
        Course course = courseRepository.findById(request.getCourseId().toString()).orElseThrow(
                () -> new NotFoundException("Course not found with id " + request.getCourseId().toString()));

        if(quizzRepository.existsByCourse(course))
            throw new BadRequestException("Quizz already exists in this course.");

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
                () -> new NotFoundException("Chapter not find with id " + request.getChapterId().toString()));
        List<Quizz> quizzByChapter = quizzRepository.findByChapter(chapter);
        Course couseByChapter = chapter.getCourse();

        boolean isNameDuplicate = quizzByChapter.stream()
                .anyMatch(quizz -> quizz.getName().equals(request.getName()));

        if (isNameDuplicate)
            throw new BadRequestException("Quizz already exists in this chapter.");

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
                .orElseThrow(() -> new NotFoundException("Course not found with id " + courseId.toString()));
        List<Quizz> quizzes = quizzRepository.findByCourse(course);

        return modelMapper.map(quizzes, new TypeToken<List<QuizzResponse>>() {
        }.getType());
    }

    @Transactional
    // search by chapter
    public List<QuizzResponse> getQuizzByChapterService(UUID chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId.toString())
                .orElseThrow(() -> new NotFoundException("Chapter not found with id " + chapterId.toString()));
        List<Quizz> quizzes = quizzRepository.findByChapter(chapter);

        return modelMapper.map(quizzes, new TypeToken<List<QuizzResponse>>() {
        }.getType());
    }

    @Transactional
    // update quizz
    public QuizzResponse updateQuizzService(UpdateQuizzRequest request) {
        Quizz quizz = quizzRepository.findById(request.getQuizzId().toString())
                .orElseThrow(() -> new NotFoundException("Quizz not found with id " + request.getQuizzId().toString()));

        List<Quizz> quizzByCourse = quizzRepository.findByCourse(quizz.getCourse());
        List<Quizz> quizzByChapter = quizzRepository.findByChapter(quizz.getChapter());

        boolean isNameDuplicateByCourse = quizzByCourse.stream()
                .anyMatch(quizz1 -> quizz1.getName().equals(request.getName()));

        boolean isNameDuplicateByChapter = quizzByChapter.stream()
                .anyMatch(quizz2 -> quizz2.getName().equals(request.getName()));

        if ((isNameDuplicateByChapter==true) || (isNameDuplicateByCourse==true))
            throw new BadRequestException("Quizz already exists.");

        quizz.setUpdatedDate(new Date());

        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(request, quizz);

        return modelMapper.map(quizzRepository.save(quizz), QuizzResponse.class);
    }

    @Transactional
    // delete
    public QuizzResponse deleteQuizzService(UUID quizzId) {
        Quizz quizz = quizzRepository.findById(quizzId.toString()).orElseThrow(
                () -> new NotFoundException("Quizz not found with id " + quizzId.toString()));

        quizzRepository.delete(quizz);
        return modelMapper.map(quizz, QuizzResponse.class);
    }
}
