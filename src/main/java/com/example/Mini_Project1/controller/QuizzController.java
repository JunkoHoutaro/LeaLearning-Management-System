package com.example.Mini_Project1.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Mini_Project1.exception.ErrorResponse;
import com.example.Mini_Project1.request.QuizzAndQuestion.CreateQuizzByChapterRequest;
import com.example.Mini_Project1.request.QuizzAndQuestion.CreateQuizzByCourseRequest;
import com.example.Mini_Project1.request.QuizzAndQuestion.UpdateQuizzRequest;
import com.example.Mini_Project1.response.QuizzAndQuestion.QuizzResponse;
import com.example.Mini_Project1.service.QuizzService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/quizzes")
@AllArgsConstructor
public class QuizzController {

    private final QuizzService quizzService;

    @PostMapping(value = "insert-by-course", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Create a new quizz by course id")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<QuizzResponse> createQuizzByCourse(@Valid @RequestBody CreateQuizzByCourseRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(quizzService.createQuizzByCourseService(request, userDetails));
    }

    @PostMapping(value = "insert-by-chapter", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Create a new quizz by chapter id")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<QuizzResponse> createQuizzByChapter(@Valid @RequestBody CreateQuizzByChapterRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(quizzService.createQuizzByChapterService(request, userDetails));
    }

    @PatchMapping(value = "update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Update a quizz")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<QuizzResponse> updateQuizz(@Valid @RequestBody UpdateQuizzRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(quizzService.updateQuizzService(request, userDetails));
    }

    @DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Delete a quizz")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<QuizzResponse> deleteQuizz(@RequestParam UUID quizzId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(quizzService.deleteQuizzService(quizzId, userDetails));
    }

    @GetMapping(value = "by-course/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get quizzes by course id")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<QuizzResponse>> getQuizzByCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(quizzService.getQuizzByCourseService(courseId));
    }

    @GetMapping(value = "by-chapter/{chapterId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get quizzes by chapter id")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<QuizzResponse>> getQuizzByChapter(@PathVariable UUID chapterId) {
        return ResponseEntity.ok(quizzService.getQuizzByChapterService(chapterId));
    }

}
