package com.example.Mini_Project1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Service
class RatingService {

    private final RatingRepository ratingRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public RatingService(RatingRepository ratingRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public Rating createRating(String courseId, RatingRequest request) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        Rating rating = Rating.builder()
                .id(null)
                .course(course)
                .user(user)
                .rating(request.getRating())
                .feedback(request.getFeedback())
                .createdDate(new Date())
                .build();

        return ratingRepository.save(rating);
    }

    public List<Rating> getRatingsByCourse(String courseId) {
        return ratingRepository.findByCourseId(courseId);
    }

    public Rating updateRating(String courseId, String ratingId, RatingRequest request) {
        Rating rating = ratingRepository.findById(ratingId).orElseThrow(() -> new RuntimeException("Rating not found"));

        if (!rating.getCourse().getId().equals(courseId)) {
            throw new RuntimeException("Course ID mismatch");
        }

        rating.setRating(request.getRating());
        rating.setFeedback(request.getFeedback());
        return ratingRepository.save(rating);
    }

    public void deleteRating(String courseId, String ratingId) {
        Rating rating = ratingRepository.findById(ratingId).orElseThrow(() -> new RuntimeException("Rating not found"));

        if (!rating.getCourse().getId().equals(courseId)) {
            throw new RuntimeException("Course ID mismatch");
        }

        ratingRepository.delete(rating);
    }
}
