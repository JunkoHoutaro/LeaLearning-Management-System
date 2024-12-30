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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

@RestController
@AllArgsConstructor
@RequestMapping("/lesson")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Create a new lesson")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<LessonResponse> createLesson(
            @Valid @RequestBody CreateLessonRequest request,
            @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        return ResponseEntity.ok(lessonService.createLesson(request, userDetails));
    }

    @GetMapping
    @Operation(summary = "Get lessons by chapter ID")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<List<LessonResponse>> getLessonsByChapter(
            @Parameter(description = "Chapter ID to get related lessons") @RequestParam UUID chapterId) {
        return ResponseEntity.ok(lessonService.getLessonsByChapter(chapterId));
    }

    @PatchMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Update lesson information")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<LessonResponse> updateLesson(@Valid @RequestBody UpdateLessonRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(lessonService.updateLesson(request, userDetails));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Delete a lesson")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<LessonResponse> deleteLesson(@RequestParam UUID lessonId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(lessonService.deleteLesson(lessonId, userDetails));
    }

    @PostMapping(value = "video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a video for the lesson")
    @ApiResponse(responseCode = "200", description = "Upload successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<FileResponse> uploadVideo(@RequestParam("file") MultipartFile file,
            @RequestParam UUID lessonId) throws Exception {
        return ResponseEntity.ok(lessonService.uploadLessonVideo(lessonId, file));
    }

    @PostMapping(value = "resource", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a resource for the lesson")
    @ApiResponse(responseCode = "200", description = "Upload successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<FileResponse> uploadResource(@RequestParam("file") MultipartFile file,
            @RequestParam UUID lessonId) throws Exception {
        return ResponseEntity.ok(lessonService.uploadLessonResource(lessonId, file));
    }
}
