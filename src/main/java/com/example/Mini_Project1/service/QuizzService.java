package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Quizz;
import com.example.Mini_Project1.repository.QuizzRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizzService {

    private QuizzRepository quizzRepository;

    // Create or Update
    public Quizz saveQuizz(Quizz quizz) {
        return quizzRepository.save(quizz);
    }

    // Read by ID
    public Optional<Quizz> getQuizzById(String id) {
        return quizzRepository.findById(id);
    }

    // Read all
    public List<Quizz> getAllQuizzes() {
        return quizzRepository.findAll();
    }

    // Delete by ID
    public void deleteQuizzById(String id) {
        quizzRepository.deleteById(id);
    }
}
