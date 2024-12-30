package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.*;
import com.example.Mini_Project1.exception.BadRequestException;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.repository.*;
import com.example.Mini_Project1.request.score.CreateScoreRequest;
import com.example.Mini_Project1.response.score.ScoreResponse;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ScoreService {
    private final ScoreRepository scoreRepository;
    private final QuizzRepository quizzRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PaymentRepository paymentRepository;
    private final CourseRepository courseRepository;

    public List<ScoreResponse> getScores(UUID userId, UUID courseId, boolean isAscending) {
        User user = userId != null ? userRepository.findById(userId.toString())
                .orElseThrow(() -> new NotFoundException("Can't find user with id " + userId)) : null;

        if(courseId != null && !courseRepository.existsById(courseId.toString()))
            throw new NotFoundException("Can't find course with id " + courseId);

        List<Quizz> quizzes = courseId != null ? quizzRepository.findByCourseId(courseId.toString()) : null;

        Specification<Score> specification = Specification.where(null);

        if (user != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("user"), user)
            );
        }

        if (quizzes != null && !quizzes.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("quizz").in(quizzes)
            );
        }

        Sort sort = isAscending ? Sort.by("score").ascending() : Sort.by("score").descending();

        List<Score> scores = scoreRepository.findAll(specification, sort);

        return modelMapper.map(scores, new TypeToken<List<ScoreResponse>>() {
        }.getType());
    }

    public ScoreResponse createScore(CreateScoreRequest request) {
        User user = userRepository.findById(request.getUserId().toString()).orElseThrow(
                () -> new NotFoundException("Can't find user with id " + request.getUserId())
        );

        Quizz quiz = quizzRepository.findById(request.getQuizId().toString()).orElseThrow(
                ()-> new NotFoundException("Can't find quiz with id " + request.getQuizId())
        );

        // Catch error if user hasn't purchased a course yet
        Hibernate.initialize(quiz.getCourse());
        if(!paymentRepository.existsByUserIdAndCourseIdAndStatus(user.getId(), quiz.getCourse().getId(), 3))
            throw new BadRequestException("This user hasn't purchased this course.");

        int totalScore = 0;

        for(Map.Entry<UUID,Character> entry: request.getAnswers().entrySet()){
            UUID questionId = entry.getKey();
            Character answer = entry.getValue();

            Question question = questionRepository.findById(questionId.toString()).orElseThrow(
                    () -> new NotFoundException("Can't find question with id " + questionId)
            );

            // Check if the question belongs to this quiz
            if(question.getQuizz() != quiz)
                throw new BadRequestException("The question with id "+ question.getId() +" doesn't belong to the quiz");

            if(answer != null && answer.equals(question.getCorrect()))
                totalScore++;
        }

        Score score = Score.builder()
                .user(user)
                .quizz(quiz)
                .score(totalScore)
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();

        Score savedScore = scoreRepository.save(score);

        return modelMapper.map(savedScore, ScoreResponse.class);
    }
}
