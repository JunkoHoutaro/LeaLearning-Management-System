package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Question;
import com.example.Mini_Project1.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private QuestionRepository questionRepository;

    // Create or Update
    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    // Read by ID
    public Optional<Question> getQuestionById(String id) {
        return questionRepository.findById(id);
    }

    // Read all
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // Delete by ID
    public void deleteQuestionById(String id) {
        questionRepository.deleteById(id);
    }
}
