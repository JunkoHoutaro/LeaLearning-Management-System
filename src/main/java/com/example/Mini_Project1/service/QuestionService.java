package com.example.Mini_Project1.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Question;
import com.example.Mini_Project1.entity.Quizz;
import com.example.Mini_Project1.exception.AccessDeniedException;
import com.example.Mini_Project1.exception.BadRequestException;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.repository.QuestionRepository;
import com.example.Mini_Project1.repository.QuizzRepository;
import com.example.Mini_Project1.request.QuizzAndQuestion.CreateQuestionRequest;
import com.example.Mini_Project1.request.QuizzAndQuestion.UpdateQuestionRequest;
import com.example.Mini_Project1.response.QuizzAndQuestion.QuestionResponse;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizzRepository quizzRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public QuestionResponse createQuestionService(CreateQuestionRequest request, UserDetails userDetails) {
        Quizz quizz = quizzRepository.findById(request.getQuizzId().toString()).orElseThrow(
                () -> new NotFoundException("Quizz not found with id " + request.getQuizzId().toString()));
        Course course = quizz.getCourse() != null ? quizz.getCourse() : quizz.getChapter().getCourse();

        if (!course.getUser().getId().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You do not have permission to create a question for this quiz.");
        }

        if (questionRepository.existsByQuizzIdAndIndex(request.getQuizzId().toString(), request.getIndex())) {
            throw new BadRequestException("This quiz already has a question with index " + request.getIndex());
        }

        if (questionRepository.existsByContentAndQuizz(request.getContent().trim(), quizz)) {
            throw new BadRequestException("This question already exists in this quiz.");
        }

        modelMapper.getConfiguration().setSkipNullEnabled(true);

        Question question = modelMapper.map(request, Question.class);
        question.setQuizz(quizz);
        question.setOptions(request.getOptions().toString());
        quizz.setUpdatedDate(new Date());

        return modelMapper.map(questionRepository.save(question), QuestionResponse.class);
    }

    @Transactional
    // get all by quizz
    public List<QuestionResponse> getAllQuestionsService(UUID quizzId) {
        Quizz quizz = quizzRepository.findById(quizzId.toString()).orElseThrow(() -> new NotFoundException("Quizz not found with id " + quizzId.toString()));
        List<Question> questions = questionRepository.findByQuizz(quizz);

        return modelMapper.map(questions, new TypeToken<List<QuestionResponse>>() {
        }.getType());
    }

    @Transactional
    public QuestionResponse updateQuestionService(UpdateQuestionRequest request, UserDetails userDetails) {
        Question question = questionRepository.findById(request.getQuestionId().toString()).orElseThrow(
                () -> new NotFoundException("Question not found with id " + request.getQuestionId().toString()));
        Quizz quizz = question.getQuizz();
        Course course = quizz.getCourse() != null ? quizz.getCourse() : quizz.getChapter().getCourse();

        if (!course.getUser().getId().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You do not have permission to update this question.");
        }

        if (questionRepository.existsByQuizzIdAndIndexExcludingCurrent(quizz.getId(), request.getIndex(), question.getId())) {
            throw new BadRequestException("This quiz already has a question with index " + request.getIndex());
        }

        if (request.getContent() != null && questionRepository.existsByContentAndQuizzExcludingCurrent(request.getContent(), quizz, question.getId())) {
            throw new BadRequestException("This question already exists in this quiz.");
        }

        modelMapper.map(request, question);

        return modelMapper.map(questionRepository.save(question), QuestionResponse.class);
    }

    @Transactional
    public QuestionResponse deleteQuestionService(UUID questionId, UserDetails userDetails) {
        Question question = questionRepository.findById(questionId.toString()).orElseThrow(
                () -> new NotFoundException("Question not found with id " + questionId.toString()));
        Quizz quizz = question.getQuizz();
        Course course = quizz.getCourse() != null ? quizz.getCourse() : quizz.getChapter().getCourse();

        if (!course.getUser().getId().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("You do not have permission to delete this question.");
        }

        questionRepository.delete(question);
        return modelMapper.map(question, QuestionResponse.class);
    }
}
