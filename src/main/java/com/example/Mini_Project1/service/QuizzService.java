package com.example.Mini_Project1.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Quizz;
import com.example.Mini_Project1.exception.AccessDeniedException;
import com.example.Mini_Project1.exception.BadRequestException;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.repository.ChapterRepository;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.repository.QuizzRepository;
import com.example.Mini_Project1.request.QuizzAndQuestion.CreateQuizzByChapterRequest;
import com.example.Mini_Project1.request.QuizzAndQuestion.CreateQuizzByCourseRequest;
import com.example.Mini_Project1.request.QuizzAndQuestion.UpdateQuizzRequest;
import com.example.Mini_Project1.response.QuizzAndQuestion.QuizzResponse;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QuizzService {

    private final QuizzRepository quizzRepository;
    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public QuizzResponse createQuizzByCourseService(CreateQuizzByCourseRequest request, UserDetails userDetails) {
        Course course = courseRepository.findById(request.getCourseId().toString()).orElseThrow(
                () -> new NotFoundException("Course not found with id " + request.getCourseId().toString()));

        if (!course.getUser().getId().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You do not have permission to create a quiz for this course.");
        }

        if (quizzRepository.existsByCourse(course)) {
            throw new BadRequestException("Quizz already exists for this course.");
        }

        modelMapper.getConfiguration().setSkipNullEnabled(true);

        Quizz quizz = modelMapper.map(request, Quizz.class);
        quizz.setCourse(course);
        quizz.setCreatedDate(new Date());
        quizz.setUpdatedDate(new Date());

        return modelMapper.map(quizzRepository.save(quizz), QuizzResponse.class);
    }

    @Transactional
    public QuizzResponse createQuizzByChapterService(CreateQuizzByChapterRequest request, UserDetails userDetails) {
        Chapter chapter = chapterRepository.findById(request.getChapterId().toString()).orElseThrow(
                () -> new NotFoundException("Chapter not found with id " + request.getChapterId().toString()));
        Course course = chapter.getCourse();

        if (!course.getUser().getId().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You do not have permission to create a quiz for this chapter.");
        }

        if (quizzRepository.existsByChapter(chapter)) {
            throw new BadRequestException("Quizz already exists for this chapter.");
        }

        modelMapper.getConfiguration().setSkipNullEnabled(true);

        Quizz quizz = modelMapper.map(request, Quizz.class);
        quizz.setChapter(chapter);
        quizz.setCreatedDate(new Date());
        quizz.setUpdatedDate(new Date());

        return modelMapper.map(quizzRepository.save(quizz), QuizzResponse.class);
    }

    @Transactional
    public QuizzResponse updateQuizzService(UpdateQuizzRequest request, UserDetails userDetails) {
        Quizz quizz = quizzRepository.findById(request.getQuizzId().toString())
                .orElseThrow(() -> new NotFoundException("Quizz not found with id " + request.getQuizzId().toString()));

        if (!quizz.getCourse().getUser().getId().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You do not have permission to update this quiz.");
        }

        modelMapper.map(request, quizz);
        quizz.setUpdatedDate(new Date());

        return modelMapper.map(quizzRepository.save(quizz), QuizzResponse.class);
    }

    @Transactional
    public QuizzResponse deleteQuizzService(UUID quizzId, UserDetails userDetails) {
        Quizz quizz = quizzRepository.findById(quizzId.toString()).orElseThrow(
                () -> new NotFoundException("Quizz not found with id " + quizzId.toString()));

        if (!quizz.getCourse().getUser().getId().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You do not have permission to delete this quiz.");
        }

        quizzRepository.delete(quizz);
        return modelMapper.map(quizz, QuizzResponse.class);
    }

    @Transactional
    public List<QuizzResponse> getQuizzByCourseService(UUID courseId) {
        Course course = courseRepository.findById(courseId.toString())
                .orElseThrow(() -> new NotFoundException("Course not found with id " + courseId.toString()));
        List<Quizz> quizzes = quizzRepository.findByCourse(course);

        return modelMapper.map(quizzes, new TypeToken<List<QuizzResponse>>() {
        }.getType());
    }

    @Transactional
    public List<QuizzResponse> getQuizzByChapterService(UUID chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId.toString())
                .orElseThrow(() -> new NotFoundException("Chapter not found with id " + chapterId.toString()));
        List<Quizz> quizzes = quizzRepository.findByChapter(chapter);

        return modelMapper.map(quizzes, new TypeToken<List<QuizzResponse>>() {
        }.getType());
    }

}
