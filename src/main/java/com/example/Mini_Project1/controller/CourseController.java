package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.enums.Action;
import com.example.Mini_Project1.enums.CourseStatus;
import com.example.Mini_Project1.enums.PaymentStatus;
import com.example.Mini_Project1.exception.ErrorResponse;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.request.course.CreateCourseRequest;
import com.example.Mini_Project1.request.course.UpdateCourseRequest;
import com.example.Mini_Project1.response.course.CourseResponse;
import com.example.Mini_Project1.response.user.UserResponse;
import com.example.Mini_Project1.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("course")
public class CourseController {
    private final CourseService courseService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new course")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Discount is optional")
//    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CourseResponse> createNewCourse(@Valid @RequestBody CreateCourseRequest request) {
        return ResponseEntity.ok(courseService.createCourse(request));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get courses by course's status")
    @ApiResponse(responseCode = "200", description = "Get successfully")
//    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<List<CourseResponse>> getCoursesByStatus(@RequestParam(required = false) CourseStatus status) {
        return ResponseEntity.ok(courseService.getCoursesByStatus(status));
    }

    @GetMapping(value ="search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Search for courses by name")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<List<CourseResponse>> getCoursesByName(@RequestParam String name, @RequestParam(required = false)CourseStatus courseStatus, @RequestParam boolean priceAscending) {
        return ResponseEntity.ok(courseService.searchCourses(name, courseStatus, priceAscending));
    }

    @GetMapping(value = "instructor", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all courses created by the instructor")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<CourseResponse>> getCoursesByInstructor(@RequestParam UUID instructorId, @RequestParam(required = false)CourseStatus courseStatus) {
        return ResponseEntity.ok(courseService.getCoursesByInstructor(instructorId, courseStatus));
    }

    @GetMapping(value = "purchase", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all courses purchased by the student")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN','STUDENT)")
    public ResponseEntity<List<CourseResponse>> getCoursesPurchased(@RequestParam UUID userId, @RequestParam(required = false)PaymentStatus paymentStatus) {
        return ResponseEntity.ok(courseService.getPurchasedCourses(userId, paymentStatus));
    }

    @PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update course's information")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'INSTRUCTOR', 'ADMIN')")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "All fields are optional, except courseId")
    public ResponseEntity<CourseResponse> updateCourse(@Valid @RequestBody UpdateCourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(request));
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete the course")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<CourseResponse> deleteCourse(@RequestParam UUID courseId) {
        return ResponseEntity.ok(courseService.deleteCourse(courseId));
    }

    @GetMapping(value = "action", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Accept or decline a course created by the instructor")
    @ApiResponse(responseCode = "200", description = "Change successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN')")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<CourseResponse> actionOnCourse(@RequestParam UUID courseId, @RequestParam Action action) {
        return ResponseEntity.ok(courseService.actionOnCourse(courseId, action));
    }

    @GetMapping(value = "student", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all students who purchased the course")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
//    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<UserResponse>> getStudentsEnroll(@RequestParam UUID courseId, @RequestParam(required = false)PaymentStatus paymentStatus) {
        return ResponseEntity.ok(courseService.getStudentsEnroll(courseId, paymentStatus));
    }
}
