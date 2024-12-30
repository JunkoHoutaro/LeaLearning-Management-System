package com.example.Mini_Project1.controller;

import java.util.List;
import java.util.UUID;

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

import com.example.Mini_Project1.request.chapter.CreateChapterRequest;
import com.example.Mini_Project1.request.chapter.UpdateChapterRequest;
import com.example.Mini_Project1.response.chapter.ChapterDetailsResponse;
import com.example.Mini_Project1.response.chapter.ChapterResponse;
import com.example.Mini_Project1.service.ChapterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/chapter")
public class ChapterController {

    private final ChapterService chapterService;

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Create a new chapter")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    public ResponseEntity<ChapterResponse> createNewChapter(@Valid @RequestBody CreateChapterRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chapterService.createChapter(request, userDetails));
    }

    @GetMapping
    @Operation(summary = "Get chapters by course ID")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Course not found")
    public ResponseEntity<List<ChapterResponse>> getChaptersByCourse(@Parameter(description = "Course ID to get related chapters") @RequestParam UUID courseId) {
        return ResponseEntity.ok(chapterService.getChaptersByCourse(courseId));
    }

    @GetMapping("/{chapterId}")
    @Operation(summary = "Get chapter details by chapter ID")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Chapter not found")
    public ResponseEntity<ChapterDetailsResponse> getChapterDetails(@PathVariable UUID chapterId) {
        return ResponseEntity.ok(chapterService.getChapterDetails(chapterId));
    }

    @PatchMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Update chapter information")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @ApiResponse(responseCode = "404", description = "Chapter not found")
    public ResponseEntity<ChapterResponse> updateChapter(@Valid @RequestBody UpdateChapterRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chapterService.updateChapter(request, userDetails));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Delete a chapter")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
    @ApiResponse(responseCode = "404", description = "Chapter not found")
    public ResponseEntity<ChapterResponse> deleteChapter(@RequestParam UUID chapterId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chapterService.deleteChapter(chapterId, userDetails));
    }

}
