package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Question;
import com.example.Mini_Project1.entity.Quizz;
import com.example.Mini_Project1.exception.BadRequestException;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.repository.QuestionRepository;
import com.example.Mini_Project1.repository.QuizzRepository;
import com.example.Mini_Project1.request.QuizzAndQuestion.CreateQuestionRequest;
import com.example.Mini_Project1.request.QuizzAndQuestion.UpdateQuestionRequest;
import com.example.Mini_Project1.response.QuizzAndQuestion.QuestionResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.List;

@Service
@AllArgsConstructor
public class QuestionService {

    private final QuestionRepository quesRepository;
    private final QuizzRepository quizzRepository;
    private final ModelMapper modelMapper;

    @Transactional
    // create question
    public QuestionResponse createQuestionService(CreateQuestionRequest request){
        Quizz quizz = quizzRepository.findById(request.getQuizzId().toString()).orElseThrow
                (()-> new NotFoundException("Quizz not found with id " + request.getQuizzId().toString()));

        if(quesRepository.existsByQuizzIdAndIndex(request.getQuizzId().toString(), request.getIndex()))
            throw new BadRequestException("This quiz already has question with index " + request.getIndex());

        if(quesRepository.existsByContentAndQuizz(request.getContent().trim(), quizz))
            throw new BadRequestException("This question has already exist on this quizz");
        else {
            modelMapper.getConfiguration().setSkipNullEnabled(true);

            Question question = modelMapper.map(request, Question.class);
            question.setQuizz(quizz);
            question.setOptions(request.getOptions().toString());
            quizz.setUpdatedDate(new Date());
            quizzRepository.save(quizz);

            return modelMapper.map(quesRepository.save(question), QuestionResponse.class);
        }
    }

    @Transactional
    // get all by quizz
    public List<QuestionResponse> getAllQuestionsService(UUID quizzId){
        Quizz quizz = quizzRepository.findById(quizzId.toString()).orElseThrow
                (()-> new NotFoundException("Quizz not found with id " + quizzId.toString()));
        List<Question> questions = quesRepository.findByQuizz(quizz);

        return modelMapper.map(questions, new TypeToken<List<QuestionResponse>>() {}.getType());
    }

    @Transactional
    // update
    public QuestionResponse updateQuestionService(UpdateQuestionRequest request) {
        Question question = quesRepository.findById(request.getQuestionId().toString()).orElseThrow(
                () -> new NotFoundException("Question not found with id " + request.getQuestionId()));
        Quizz quizz = quizzRepository.findById(question.getQuizz().getId().toString()).orElseThrow(
                () -> new NotFoundException("Quizz not found with id " + question.getQuizz().getId().toString()));

        if(quesRepository.existsByQuizzIdAndIndex(quizz.getId(), request.getIndex()))
            throw new BadRequestException("This quiz already has question with index " + request.getIndex());

        if (request.getContent() != null && quesRepository.existsByContentAndQuizz(request.getContent(), quizz)) {
            throw new BadRequestException("This question has already exist on this quizz");
        }
        quizz.setUpdatedDate(new Date());
        if(request.getOptions() != null)
            question.setOptions(request.getOptions().toString());

        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(request, question);

        return modelMapper.map(quesRepository.save(question), QuestionResponse.class);
    }

    @Transactional
    // delete
    public QuestionResponse deleteQuestionService(UUID questionId){
        Question question = quesRepository.findById(questionId.toString()).orElseThrow
                (() -> new NotFoundException("Question not found with id " + questionId.toString()));
        Quizz quizz = quizzRepository.findById(question.getQuizz().getId().toString()).orElseThrow(
                () -> new NotFoundException("Quizz not found with id " + question.getQuizz().getId().toString()));
        quizz.setUpdatedDate(new Date());
        quizzRepository.save(quizz);

        quesRepository.delete(question);
        return modelMapper.map(question, QuestionResponse.class);
    }
}
