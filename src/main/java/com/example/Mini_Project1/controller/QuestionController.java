package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.request.QuizzAndQuestion.CreateQuestionRequest;
import com.example.Mini_Project1.request.QuizzAndQuestion.UpdateQuestionRequest;
import com.example.Mini_Project1.response.QuizzAndQuestion.QuestionResponse;
import com.example.Mini_Project1.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/questions")
@AllArgsConstructor
public class QuestionController {

    private QuestionService questionService;

    // create
    @PostMapping("insert")
    @Operation(summary = "Create a new question")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody CreateQuestionRequest request) {
        return ResponseEntity.ok(questionService.createQuestionService(request));
    }

    // get all questions by quizz
    @GetMapping("get-all-quesiton")
    @Operation(summary = "Search all question by quizz")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<List<QuestionResponse>> getAllQuestions(@RequestParam UUID quizzId) {
        return ResponseEntity.ok(questionService.getAllQuestionsService(quizzId));
    }

    // update
    @PatchMapping("update")
    @Operation(summary = "Update  a question")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    public ResponseEntity<QuestionResponse> updateQuestion(@Valid @RequestBody UpdateQuestionRequest request) {
        return ResponseEntity.ok(questionService.updateQuestionService(request));
    }

    // Delete
    @DeleteMapping("delete")
    @Operation(summary = "Delete a question")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
    public ResponseEntity<QuestionResponse> deleteQuestion(@RequestParam UUID questionId) {
        return ResponseEntity.ok(questionService.deleteQuestionService(questionId));
    }
}
