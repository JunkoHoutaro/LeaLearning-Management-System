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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/questions")
@AllArgsConstructor
public class QuestionController {

    private QuestionService questionService;

    @PostMapping(value = "insert", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Create a new question")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody CreateQuestionRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(questionService.createQuestionService(request, userDetails));
    }

    // get all questions by quizz
    @GetMapping(value = "get-all-quesiton", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Search all question by quizz")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<List<QuestionResponse>> getAllQuestions(@RequestParam UUID quizzId) {
        return ResponseEntity.ok(questionService.getAllQuestionsService(quizzId));
    }

    @PatchMapping(value = "update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Update a question")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<QuestionResponse> updateQuestion(@Valid @RequestBody UpdateQuestionRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(questionService.updateQuestionService(request, userDetails));
    }

    @DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Delete a question")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<QuestionResponse> deleteQuestion(@RequestParam UUID questionId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(questionService.deleteQuestionService(questionId, userDetails));
    }
}
