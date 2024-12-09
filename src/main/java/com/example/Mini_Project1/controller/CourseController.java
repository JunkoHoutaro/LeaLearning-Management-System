package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.enums.Action;
import com.example.Mini_Project1.exception.ErrorResponse;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.request.course.CreateCourseRequest;
import com.example.Mini_Project1.request.course.UpdateCourseRequest;
import com.example.Mini_Project1.response.course.CourseResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("v1/course")
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    @Operation(summary = "Create a new course")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Discount is optional")
//    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<CourseResponse> createNewCourse(@Valid @RequestBody CreateCourseRequest request) {
        return ResponseEntity.ok(courseService.createCourse(request));
    }

    @GetMapping
    @Operation(summary = "Get courses by course's status")
    @ApiResponse(responseCode = "200", description = "Get successfully")
//    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<List<CourseResponse>> getCoursesByStatus( @Parameter(description = "If the status is null, return all courses")
                                                                        @RequestParam(required = false) Integer status) {
        return ResponseEntity.ok(courseService.getCoursesByStatus(status));
    }

    @GetMapping("search")
    @Operation(summary = "Search for courses by name")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<List<CourseResponse>> getCoursesByName(@RequestParam String name, @RequestParam boolean priceAscending) {
        return ResponseEntity.ok(courseService.searchCourses(name,priceAscending));
    }

    @GetMapping("instructor")
    @Operation(summary = "Get all courses created by the instructor")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<List<CourseResponse>> getCoursesByInstructor(@RequestParam UUID instructorId) {
        return ResponseEntity.ok(courseService.getCoursesByInstructor(instructorId));
    }

    @GetMapping("purchase")
    @Operation(summary = "Get all courses purchased by the student")
    @ApiResponse(responseCode = "200", description = "Get successfully")
//    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN','STUDENT)")
    public ResponseEntity<List<CourseResponse>> getCoursesPurchased(@RequestParam UUID userId) {
        return ResponseEntity.ok(courseService.getPurchasedCourses(userId));
    }

    @PatchMapping
    @Operation(summary = "Update course's information")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
//    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'INSTRUCTOR', 'ADMIN')")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "All fields are optional except courseId")
    public ResponseEntity<CourseResponse> updateCourse(@Valid @RequestBody UpdateCourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(request));
    }

    @DeleteMapping
    @Operation(summary = "Delete the course")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
//    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<CourseResponse> deleteCourse(@RequestParam UUID courseId) {
        return ResponseEntity.ok(courseService.deleteCourse(courseId));
    }

    @GetMapping("action")
    @Operation(summary = "Accept or decline a course created by the instructor")
    @ApiResponse(responseCode = "200", description = "Change successfully")
//    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN')")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<CourseResponse> actionOnCourse(@RequestParam UUID courseId, @RequestParam Action action) {
        return ResponseEntity.ok(courseService.actionOnCourse(courseId, action));
    }
}
