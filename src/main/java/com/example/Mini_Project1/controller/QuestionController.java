package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.entity.Question;
import com.example.Mini_Project1.request.QuizzAndQuestion.CreateQuestionRequest;
import com.example.Mini_Project1.request.QuizzAndQuestion.UpdateQuestionRequest;
import com.example.Mini_Project1.response.QuizzAndQuestion.QuestionResponse;
import com.example.Mini_Project1.service.QuestionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/questions")
@RequiredArgsConstructor
@AllArgsConstructor
public class QuestionController {

    private QuestionService questionService;

    // create
    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody CreateQuestionRequest request) {
        return ResponseEntity.ok(questionService.createQuestionService(request));
    }

    // read all
    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestionsService());
    }

    // read by correct status
    @GetMapping("/{correct}")
    public ResponseEntity<List<QuestionResponse>> getQuestionByCorrect(@PathVariable Character correct) {
        return ResponseEntity.ok(questionService.getQuestionByCorrectService(correct));
    }

    // update
    @PostMapping
    public ResponseEntity<QuestionResponse> updateQuestion(@Valid @RequestBody UpdateQuestionRequest request) {
        return ResponseEntity.ok(questionService.updateQuestionService(request));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<QuestionResponse> deleteQuestion(@RequestParam String id) {
        return ResponseEntity.ok(questionService.deleteQuestionService(id));
    }
}
