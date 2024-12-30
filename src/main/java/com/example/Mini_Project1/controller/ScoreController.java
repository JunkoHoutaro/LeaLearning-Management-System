package com.example.Mini_Project1.controller;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("score")
public class ScoreController {
    private final ScoreService scoreService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Create a new result based on user answers")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ScoreResponse> createScore(@Valid @RequestBody CreateScoreRequest createScoreRequest) {
        return ResponseEntity.ok(scoreService.createScore(createScoreRequest));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Get results with filter")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<ScoreResponse>> getScores(@RequestParam(required = false) UUID userId,
                                                         @RequestParam(required = false) UUID courseId,
                                                         @RequestParam boolean isAscending) {
        return ResponseEntity.ok(scoreService.getScores(userId, courseId, isAscending));
    }
}
