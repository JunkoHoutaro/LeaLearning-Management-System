package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.request.QuizzAndQuestion.*;
import com.example.Mini_Project1.response.QuizzAndQuestion.QuizzResponse;
import com.example.Mini_Project1.service.QuizzService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.models.annotations.OpenAPI30;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/quizzes")
@AllArgsConstructor
public class QuizzController {

    private QuizzService quizzService;

    // creat quizz by course id
    @PostMapping("insert-by-course")
    @Operation(summary = "Create a new quizz by course id")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    public ResponseEntity<QuizzResponse> createQuizzByCourse(@Valid @RequestBody CreateQuizzByCourseRequest request){
        return ResponseEntity.ok(quizzService.createQuizzByCourseService(request));
    }

    // creat quizz by chapter id
    @PostMapping("insert-by-chapter")
    @Operation(summary = "Create a new quizz by chapter id")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    public ResponseEntity<QuizzResponse> createQuizzByChapter(@Valid @RequestBody CreateQuizzByChapterRequest request){
        return ResponseEntity.ok(quizzService.createQuizzByChapterService(request));
    }

    // get quizzes by course
    @GetMapping("search-by-course")
    @Operation(summary = "Search all quizzes by course")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<List<QuizzResponse>> getQuizzByCourse(@RequestParam UUID courseId){
        return ResponseEntity.ok(quizzService.getQuizzByCourseService(courseId));
    }

    // get quizzes by chapter
    @GetMapping("search-by-chapter")
    @Operation(summary = "Search all quizzes by chapter")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<List<QuizzResponse>> getQuizzByChapter(@RequestParam UUID chapterId){
        return ResponseEntity.ok(quizzService.getQuizzByChapterService(chapterId));
    }

    // update quizz
    @PatchMapping("update")
    @Operation(summary = "Update a quizz")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    public ResponseEntity<QuizzResponse> updateQuizz(@Valid @RequestBody UpdateQuizzRequest request){
        return ResponseEntity.ok(quizzService.updateQuizzService(request));
    }

    // delete
    @DeleteMapping("delete")
    @Operation(summary = "Delete a quizz")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
    public ResponseEntity<QuizzResponse> deleteQuizz(@RequestParam UUID quizzId) {
        return ResponseEntity.ok(quizzService.deleteQuizzService(quizzId));
    }
}
