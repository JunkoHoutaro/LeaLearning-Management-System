package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.request.lesson.CreateLessonRequest;
import com.example.Mini_Project1.request.lesson.UpdateLessonRequest;
import com.example.Mini_Project1.response.lesson.LessonResponse;
import com.example.Mini_Project1.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("v1/lesson")
public class LessonController {
    private final LessonService lessonService;

    @PostMapping
    @Operation(summary = "Create a new lesson")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    public ResponseEntity<LessonResponse> createNewLesson(@Valid @RequestBody CreateLessonRequest request) {
        return ResponseEntity.ok(lessonService.createLesson(request));
    }

    @GetMapping
    @Operation(summary = "Get lessons by chapter ID")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<List<LessonResponse>> getLessonsByChapter(
            @Parameter(description = "Chapter ID to get related lessons") @RequestParam UUID chapterId) {
        return ResponseEntity.ok(lessonService.getLessonsByChapter(chapterId));
    }

    @PatchMapping
    @Operation(summary = "Update lesson information")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    public ResponseEntity<LessonResponse> updateLesson(@Valid @RequestBody UpdateLessonRequest request) {
        return ResponseEntity.ok(lessonService.updateLesson(request));
    }

    @DeleteMapping
    @Operation(summary = "Delete a lesson")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
    public ResponseEntity<LessonResponse> deleteLesson(@RequestParam UUID lessonId) {
        return ResponseEntity.ok(lessonService.deleteLesson(lessonId));
    }
}
