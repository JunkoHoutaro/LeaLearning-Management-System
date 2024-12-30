package com.example.Mini_Project1.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Mini_Project1.exception.BadRequestException;
import com.example.Mini_Project1.exception.ErrorResponse;
import com.example.Mini_Project1.request.score.CreateScoreRequest;
import com.example.Mini_Project1.response.score.ScoreResponse;
import com.example.Mini_Project1.service.ScoreService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/score")
public class ScoreController {

    private final ScoreService scoreService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit quiz answers and get score")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ScoreResponse> createScore(
            @Valid @RequestBody CreateScoreRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (scoreService.hasStudentTakenQuiz(request.getQuizzId(), UUID.fromString(userDetails.getUsername()))) {
            throw new BadRequestException("You have already taken this quiz");
        }
        return ResponseEntity.ok(scoreService.createScore(request, userDetails));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all scores for a quiz")
    @ApiResponse(responseCode = "200", description = "Scores retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Quiz not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<ScoreResponse>> getAllScores(@RequestParam UUID quizzId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(scoreService.getAllScores(quizzId));
    }

    @GetMapping(value = "/student", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get student's score for a quiz")
    @ApiResponse(responseCode = "200", description = "Score retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Score not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ScoreResponse> getStudentScore(
            @RequestParam UUID quizzId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(scoreService.getStudentScore(quizzId, UUID.fromString(userDetails.getUsername())));
    }
}
