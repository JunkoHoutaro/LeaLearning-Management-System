package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.request.chapter.CreateChapterRequest;
import com.example.Mini_Project1.request.chapter.UpdateChapterRequest;
import com.example.Mini_Project1.response.chapter.ChapterResponse;
import com.example.Mini_Project1.service.ChapterService;
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
@RequestMapping("/chapter")
public class ChapterController {
    private final ChapterService chapterService;

    @PostMapping
    @Operation(summary = "Create a new chapter")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    public ResponseEntity<ChapterResponse> createNewChapter(@Valid @RequestBody CreateChapterRequest request) {
        return ResponseEntity.ok(chapterService.createChapter(request));
    }

    @GetMapping
    @Operation(summary = "Get chapters by course ID")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<List<ChapterResponse>> getChaptersByCourse(
            @Parameter(description = "Course ID to get related chapters") @RequestParam UUID courseId) {

        // Chuyển đổi courseId từ String thành UUID nếu cần
        // UUID courseUUID = UUID.fromString(courseId);

        // Gọi service với courseId đã chuyển đổi
        return ResponseEntity.ok(chapterService.getChaptersByCourse(courseId));
    }

    @PatchMapping
    @Operation(summary = "Update chapter information")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    public ResponseEntity<ChapterResponse> updateChapter(@Valid @RequestBody UpdateChapterRequest request) {
        return ResponseEntity.ok(chapterService.updateChapter(request));
    }

    @DeleteMapping
    @Operation(summary = "Delete a chapter")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
    public ResponseEntity<ChapterResponse> deleteChapter(@RequestParam UUID chapterId) {
        return ResponseEntity.ok(chapterService.deleteChapter(chapterId));
    }
}
