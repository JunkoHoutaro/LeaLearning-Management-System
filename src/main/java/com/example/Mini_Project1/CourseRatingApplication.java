package com.example.Mini_Project1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@SpringBootApplication
public class CourseRatingApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseRatingApiApplication.class, args);
    }
}

@RestController
@RequestMapping("/api/admin/courses/{courseId}/ratings")
class CourseRatingController {

    private final RatingService ratingService;

    public CourseRatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<Rating> createRating(@PathVariable String courseId, @RequestBody RatingRequest request) {
        Rating newRating = ratingService.createRating(courseId, request);
        return ResponseEntity.status(201).body(newRating);
    }

    @GetMapping
    public ResponseEntity<List<Rating>> getRatings(@PathVariable String courseId) {
        List<Rating> ratings = ratingService.getRatingsByCourse(courseId);
        return ResponseEntity.ok(ratings);
    }

    @PutMapping("/{ratingId}")
    public ResponseEntity<Rating> updateRating(@PathVariable String courseId, @PathVariable String ratingId, @RequestBody RatingRequest request) {
        Rating updatedRating = ratingService.updateRating(courseId, ratingId, request);
        return ResponseEntity.ok(updatedRating);
    }

    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteRating(@PathVariable String courseId, @PathVariable String ratingId) {
        ratingService.deleteRating(courseId, ratingId);
        return ResponseEntity.ok().build();
    }
}

@Repository
interface RatingRepository extends JpaRepository<Rating, String> {
    List<Rating> findByCourseId(String courseId);
}

@Repository
interface CourseRepository extends JpaRepository<Course, String> {}

@Repository
interface UserRepository extends JpaRepository<User, String> {}
