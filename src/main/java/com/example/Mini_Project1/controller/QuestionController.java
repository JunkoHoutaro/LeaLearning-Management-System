package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.exception.ErrorResponse;
import com.example.Mini_Project1.request.QuizzAndQuestion.CreateQuestionRequest;
import com.example.Mini_Project1.request.QuizzAndQuestion.UpdateQuestionRequest;
import com.example.Mini_Project1.response.QuizzAndQuestion.QuestionResponse;
import com.example.Mini_Project1.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
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
    @PostMapping(value = "insert", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new question")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody CreateQuestionRequest request) {
        return ResponseEntity.ok(questionService.createQuestionService(request));
    }

    // get all questions by quizz
    @GetMapping(value = "get-all-quesiton", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Search all question by quizz")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<List<QuestionResponse>> getAllQuestions(@RequestParam UUID quizzId) {
        return ResponseEntity.ok(questionService.getAllQuestionsService(quizzId));
    }

    // update
    @PatchMapping(value = "update", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update  a question")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<QuestionResponse> updateQuestion(@Valid @RequestBody UpdateQuestionRequest request) {
        return ResponseEntity.ok(questionService.updateQuestionService(request));
    }

    // Delete
    @DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete a question")
    @ApiResponse(responseCode = "200", description = "Delete successfully", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<QuestionResponse> deleteQuestion(@RequestParam UUID questionId) {
        return ResponseEntity.ok(questionService.deleteQuestionService(questionId));
    }
}
