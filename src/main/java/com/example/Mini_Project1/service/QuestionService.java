package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Question;
import com.example.Mini_Project1.entity.Quizz;
import com.example.Mini_Project1.repository.QuestionRepository;
import com.example.Mini_Project1.repository.QuizzRepository;
import com.example.Mini_Project1.request.QuizzAndQuestion.CreateQuestionRequest;
import com.example.Mini_Project1.request.QuizzAndQuestion.UpdateQuestionRequest;
import com.example.Mini_Project1.response.QuizzAndQuestion.QuestionResponse;
import com.example.Mini_Project1.response.QuizzAndQuestion.QuizzResponse;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import java.util.List;

public class QuestionService {
    private QuestionRepository quesRepository;
    private QuizzRepository quizzRepository;

    @Bean
    // create
    public QuestionResponse createQuestionService(CreateQuestionRequest request){
        Quizz quizz = quizzRepository.findById(request.getQuizzId().toString()).orElseThrow
                (()-> new RuntimeException("Can't find quizz with id " + request.getQuizzId().toString()));

        if(quesRepository.existsByContentAndQuizz(request.getContent().trim(), quizz))
            throw new RuntimeException("This question has already exist on this quizz");
        else {
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setSkipNullEnabled(true);

            Question question = modelMapper.map(request, Question.class);
            question.setQuizz(quizz);
            quizz.setUpdatedDate(new Date());

            return modelMapper.map(quesRepository.save(question), QuestionResponse.class);
        }
    }

    // read all
    public List<QuestionResponse> getAllQuestionsService(){
        List<Question> questions = quesRepository.findAll();
        return new ModelMapper().map(questions, new TypeToken<List<QuestionResponse>>() {}.getType());
    }

    // read by correct status
    public List<QuestionResponse> getQuestionByCorrectService (Character correct){
        if(correct == null){
            List<Question> questions = quesRepository.findAll();
            return new ModelMapper().map(questions, new TypeToken<List<QuestionResponse>>() {}.getType());
        }
        List<Question> questions = quesRepository.findByCorrect(correct);
        return new ModelMapper().map(questions, new TypeToken<List<QuestionResponse>>() {}.getType());
    }

    // update
    public QuestionResponse updateQuestionService(UpdateQuestionRequest request) {
        Question question = quesRepository.findById(request.getQuestionId().toString()).orElseThrow(
                () -> new RuntimeException("Can't find question with id " + request.getQuestionId()));
        Quizz quizz = quizzRepository.findById(request.getQuizzId().toString()).orElseThrow(
                () -> new RuntimeException("Can't find quizz with id " + request.getQuizzId().toString()));
        if (request.getContent() != null && quesRepository.existsByContentAndQuizz(request.getContent(), quizz)) {
            throw new RuntimeException("This question has already exist on this quizz");
        }
        quizz.setUpdatedDate(new Date());

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(request, question);

        return modelMapper.map(quesRepository.save(question), QuestionResponse.class);
    }

    // delete
    public QuestionResponse deleteQuestionService(String questionId){
        Question question = quesRepository.findById(questionId.toString()).orElseThrow
                (() -> new RuntimeException("Can't find question with id " + questionId.toString()));

        question.getQuizz().setUpdatedDate(new Date());
        quesRepository.deleteById(questionId);
        return new ModelMapper().map(quesRepository.save(question), QuestionResponse.class);
    }
}
