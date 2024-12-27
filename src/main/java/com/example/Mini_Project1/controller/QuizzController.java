package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.exception.ErrorResponse;
import com.example.Mini_Project1.request.QuizzAndQuestion.*;
import com.example.Mini_Project1.response.QuizzAndQuestion.QuizzResponse;
import com.example.Mini_Project1.service.QuizzService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.models.annotations.OpenAPI30;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/quizzes")
@AllArgsConstructor
public class QuizzController {

    private QuizzService quizzService;

    // creat quizz by course id
    @PostMapping(value = "insert-by-course", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new quizz by course id")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<QuizzResponse> createQuizzByCourse(@Valid @RequestBody CreateQuizzByCourseRequest request){
        return ResponseEntity.ok(quizzService.createQuizzByCourseService(request));
    }

    // creat quizz by chapter id
    @PostMapping(value = "insert-by-chapter", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new quizz by chapter id")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<QuizzResponse> createQuizzByChapter(@Valid @RequestBody CreateQuizzByChapterRequest request){
        return ResponseEntity.ok(quizzService.createQuizzByChapterService(request));
    }

    // get quizzes by course
    @GetMapping(value = "search-by-course", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Search all quizzes by course")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<List<QuizzResponse>> getQuizzByCourse(@RequestParam UUID courseId){
        return ResponseEntity.ok(quizzService.getQuizzByCourseService(courseId));
    }

    // get quizzes by chapter
    @GetMapping(value = "search-by-chapter", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Search all quizzes by chapter")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<List<QuizzResponse>> getQuizzByChapter(@RequestParam UUID chapterId){
        return ResponseEntity.ok(quizzService.getQuizzByChapterService(chapterId));
    }

    // update quizz
    @PatchMapping(value = "update", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update a quizz")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<QuizzResponse> updateQuizz(@Valid @RequestBody UpdateQuizzRequest request){
        return ResponseEntity.ok(quizzService.updateQuizzService(request));
    }

    // delete
    @DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete a quizz")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<QuizzResponse> deleteQuizz(@RequestParam UUID quizzId) {
        return ResponseEntity.ok(quizzService.deleteQuizzService(quizzId));
    }
}
