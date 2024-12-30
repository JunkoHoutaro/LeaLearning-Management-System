package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.exception.ErrorResponse;
import com.example.Mini_Project1.request.lesson.CreateLessonRequest;
import com.example.Mini_Project1.request.lesson.UpdateLessonRequest;
import com.example.Mini_Project1.response.file.FileResponse;
import com.example.Mini_Project1.response.lesson.LessonResponse;
import com.example.Mini_Project1.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/lesson")
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
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<LessonResponse> updateLesson(@Valid @RequestBody UpdateLessonRequest request) {
        return ResponseEntity.ok(lessonService.updateLesson(request));
    }

    @DeleteMapping
    @Operation(summary = "Delete a lesson")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<LessonResponse> deleteLesson(@RequestParam UUID lessonId) {
        return ResponseEntity.ok(lessonService.deleteLesson(lessonId));
    }

    @PostMapping(value = "video", consumes = "multipart/form-data")
    @Operation(summary = "Upload a video for the lesson")
    @ApiResponse(responseCode = "200", description = "Upload successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<FileResponse> uploadVideo(@RequestParam("file") MultipartFile file,
            @RequestParam UUID lessonId) throws Exception {
        return ResponseEntity.ok(lessonService.uploadLessonVideo(lessonId, file));
    }

    @PostMapping(value = "resource", consumes = "multipart/form-data")
    @Operation(summary = "Upload a resource for the lesson")
    @ApiResponse(responseCode = "200", description = "Upload successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<FileResponse> uploadResource(@RequestParam("file") MultipartFile file,
            @RequestParam UUID lessonId) throws Exception {
        return ResponseEntity.ok(lessonService.uploadLessonResource(lessonId, file));
    }
}
