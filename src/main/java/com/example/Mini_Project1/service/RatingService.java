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
    
        Optional<Rating> existingRatingOpt = ratingRepository.findByCourseAndUser(course, user);
        
        if (existingRatingOpt.isPresent()) {
            Rating existingRating = existingRatingOpt.get();
    
            existingRating.setRating(request.getRating());
            existingRating.setFeedback(request.getFeedback());
            existingRating.setCreatedDate(new Date());
            ratingRepository.save(existingRating);
    
            RatingHistory newHistory = RatingHistory.builder()
                    .course(existingRating.getCourse())
                    .user(existingRating.getUser())
                    .rating(request.getRating())
                    .feedback(request.getFeedback())
                    .createdDate(existingRating.getCreatedDate())
                    .build();
            ratingHistoryRepository.save(newHistory);
    
            return mapToResponse(existingRating);
        } else {
            Rating newRating = Rating.builder()
                    .course(course)
                    .user(user)
                    .rating(request.getRating())
                    .feedback(request.getFeedback())
                    .createdDate(new Date())
                    .build();
            ratingRepository.save(newRating);
    
            RatingHistory newHistory = RatingHistory.builder()
                    .course(newRating.getCourse())
                    .user(newRating.getUser())
                    .rating(newRating.getRating())
                    .feedback(newRating.getFeedback())
                    .createdDate(newRating.getCreatedDate())
                    .build();
            ratingHistoryRepository.save(newHistory);
    
            return mapToResponse(newRating);
        }
    }
            
    public CourseRatingResponse getRatingsByCourseId(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found!"));

        List<Rating> ratings = ratingRepository.findByCourse(course);
        double averageRating = ratings.stream()
                .mapToDouble(Rating::getRating)
                .average()
                .orElse(0.0);

        int totalRatings = ratings.size();

        List<RatingResponse> ratingResponses = ratings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return CourseRatingResponse.builder()
                .courseName(course.getName())
                .averageRating(averageRating)
                .totalRatings(totalRatings)
                .ratings(ratingResponses)
                .build();
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
                .userName(rating.getUser().getName())
                .userId(rating.getUser().getId())
                .createdDate(rating.getCreatedDate())
                .build();
    }
}
