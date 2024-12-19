package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.request.rating.RatingCreateRequest;
import com.example.Mini_Project1.request.rating.RatingUpdateRequest;
import com.example.Mini_Project1.response.rating.CourseRatingResponse;
import com.example.Mini_Project1.response.rating.RatingResponse;
import com.example.Mini_Project1.service.RatingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @PostMapping
    @Operation(summary = "Create a new rating", description = "Create a rating for a course by a user.")
    @ApiResponse(responseCode = "200", description = "Rating created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    public ResponseEntity<RatingResponse> createRating(
            @Valid @RequestBody @Parameter(description = "Rating request body containing rating details") RatingCreateRequest request) {
        return ResponseEntity.ok(ratingService.createRating(request));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get ratings by course ID", description = "Get average rating and all ratings for a course by its ID.")
    @ApiResponse(responseCode = "200", description = "Ratings retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Course not found")
    public ResponseEntity<CourseRatingResponse> getRatingsByCourseId(
            @PathVariable String courseId) {
        CourseRatingResponse courseRatingResponse = ratingService.getRatingsByCourseId(courseId);
        return ResponseEntity.ok(courseRatingResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a rating", description = "Delete a rating by its ID and return the deleted rating.")
    @ApiResponse(responseCode = "200", description = "Rating deleted successfully", content = @Content(schema = @Schema(implementation = RatingResponse.class)))
    @ApiResponse(responseCode = "404", description = "Rating not found")
    public ResponseEntity<RatingResponse> deleteRating(
            @PathVariable @Parameter(description = "ID of the rating to delete") String id) {
        RatingResponse deletedRating = ratingService.deleteRating(id);
        return ResponseEntity.ok(deletedRating);
    }
}