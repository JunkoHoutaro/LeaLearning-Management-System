package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.entity.Question;
import com.example.Mini_Project1.entity.Quizz;
import com.example.Mini_Project1.request.QuizzAndQuestion.CreateQuizzRequest;
import com.example.Mini_Project1.request.QuizzAndQuestion.UpdateQuizzRequest;
import com.example.Mini_Project1.response.QuizzAndQuestion.QuizzResponse;
import com.example.Mini_Project1.service.QuizzService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/quizzes")
@RequiredArgsConstructor
@AllArgsConstructor
public class QuizzController {

    private QuizzService quizzService;

    // create
    @PostMapping
    public ResponseEntity<QuizzResponse> createQuizz(@Valid @RequestBody CreateQuizzRequest request) {
        return ResponseEntity.ok(quizzService.createQuizzService(request));
    }

    // read all
    @GetMapping
    public ResponseEntity<List<QuizzResponse>> getAllQuizzes() {
        return ResponseEntity.ok(quizzService.getAllQuizzesService());
    }

    // read by name
    @GetMapping("/{name}")
    public ResponseEntity<List<QuizzResponse>> getQuizz(@PathVariable String name) {
        return ResponseEntity.ok(quizzService.getQuestionByNameService(name));

    }
    // update
    public ResponseEntity<QuizzResponse> updateQuizz(@Valid @RequestBody UpdateQuizzRequest request) {
        return ResponseEntity.ok(quizzService.updateQuizzService(request));
    }

    // delete
    @DeleteMapping("/{id}")
    public ResponseEntity<QuizzResponse> deleteQuizz(@RequestParam String id) {
        return ResponseEntity.ok(quizzService.deleteQuizzService(id));
    }
}
