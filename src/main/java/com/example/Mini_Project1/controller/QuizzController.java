package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.entity.Quizz;
import com.example.Mini_Project1.service.QuizzService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizzController {

    private QuizzService quizzService;

    @PostMapping
    public ResponseEntity<Quizz> createQuizz(@RequestBody Quizz quizz) {
        return ResponseEntity.ok(quizzService.saveQuizz(quizz));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quizz> getQuizz(@PathVariable String id) {
        return quizzService.getQuizzById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Quizz>> getAllQuizzes() {
        return ResponseEntity.ok(quizzService.getAllQuizzes());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuizz(@PathVariable String id) {
        quizzService.deleteQuizzById(id);
        return ResponseEntity.noContent().build();
    }
}
