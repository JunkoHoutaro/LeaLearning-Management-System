package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Rating;
import com.example.Mini_Project1.entity.RatingHistory;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.repository.RatingCourseRepository;
import com.example.Mini_Project1.repository.RatingHistoryRepository;
import com.example.Mini_Project1.repository.RatingRepository;
import com.example.Mini_Project1.repository.UserRepository;
import com.example.Mini_Project1.request.rating.RatingCreateRequest;
import com.example.Mini_Project1.request.rating.RatingUpdateRequest;
import com.example.Mini_Project1.response.rating.CourseRatingResponse;
import com.example.Mini_Project1.response.rating.RatingResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private RatingCourseRepository ratingCourseRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RatingHistoryRepository ratingHistoryRepository;

    public RatingResponse createRating(RatingCreateRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found!"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Rating rating = Rating.builder()
                .course(course)
                .user(user)
                .rating(request.getRating())
                .feedback(request.getFeedback())
                .createdDate(new Date())
                .build();
        ratingRepository.save(rating);

        return mapToResponse(rating);
    }

    public CourseRatingResponse getRatingsByRatingId(String ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Rating not found!"));
        Course course = rating.getCourse();
        List<Course> courses = ratingCourseRepository.findByName(course.getName());
        if (courses.isEmpty()) {
            return CourseRatingResponse.builder()
                    .courseName(course.getName())
                    .averageRating(0.0)
                    // giống
                    .ratings(List.of(mapToRatingResponse(rating)))
                    .build();
        }

        List<Rating> allRatings = courses.stream()
                .flatMap(c -> ratingRepository.findByCourse(c).stream())
                .collect(Collectors.toList());
        double averageRating = allRatings.stream()
                .mapToDouble(Rating::getRating)
                .average()
                .orElse(0.0);
        List<RatingResponse> ratingResponses = allRatings.stream()
                .map(this::mapToRatingResponse)
                .collect(Collectors.toList());
        return CourseRatingResponse.builder()
                .courseName(course.getName())
                .averageRating(averageRating)
                .ratings(ratingResponses)
                .build();
    }

    public RatingResponse updateRating(String id, RatingUpdateRequest request) {
        Rating existingRating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating not found!"));
        RatingHistory ratingHistory = RatingHistory.builder()
                .course(existingRating.getCourse())
                .user(existingRating.getUser())
                .rating(existingRating.getRating())
                .feedback(existingRating.getFeedback())
                .createdDate(existingRating.getCreatedDate())
                .build();
        ratingHistoryRepository.save(ratingHistory);
        existingRating.setRating(request.getRating());
        existingRating.setFeedback(request.getFeedback());
        existingRating.setCreatedDate(new Date());
        ratingRepository.save(existingRating);

        return mapToResponse(existingRating);
    }

    public RatingResponse deleteRating(String id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating not found!"));
        RatingResponse response = mapToResponse(rating);
        ratingRepository.delete(rating);
        return response;
    }

    private RatingResponse mapToResponse(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .rating(rating.getRating())
                .feedback(rating.getFeedback())
                .courseName(rating.getCourse().getName())
                .createdDate(rating.getCreatedDate())
                .userName(rating.getUser().getName())
                .build();
    }

    private RatingResponse mapToRatingResponse(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .rating(rating.getRating())
                .feedback(rating.getFeedback())
                .courseName(rating.getCourse().getName())
                .createdDate(rating.getCreatedDate())
                .userName(rating.getUser().getName())
                .build();
    }
}
