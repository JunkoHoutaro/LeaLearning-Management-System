package com.example.Mini_Project1.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Question;
import com.example.Mini_Project1.entity.Quizz;
import com.example.Mini_Project1.entity.Score;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.exception.BadRequestException;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.repository.PaymentRepository;
import com.example.Mini_Project1.repository.QuestionRepository;
import com.example.Mini_Project1.repository.QuizzRepository;
import com.example.Mini_Project1.repository.ScoreRepository;
import com.example.Mini_Project1.repository.UserRepository;
import com.example.Mini_Project1.request.score.CreateScoreRequest;
import com.example.Mini_Project1.response.score.ScoreResponse;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ScoreService {

    private final ScoreRepository scoreRepository;
    private final QuizzRepository quizzRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PaymentRepository paymentRepository;

    public boolean hasStudentTakenQuiz(UUID quizzId, UUID userId) {
        Quizz quizz = quizzRepository.findById(quizzId.toString())
                .orElseThrow(() -> new NotFoundException("Quiz not found"));
        User user = userRepository.findById(userId.toString())
                .orElseThrow(() -> new NotFoundException("User not found"));

        return scoreRepository.findOne(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("quizz"), quizz),
                        cb.equal(root.get("user"), user)
                )
        ).isPresent();
    }

    private boolean hasUserPurchasedCourse(String userId, String courseId) {
        return paymentRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    private Map<UUID, Character> validateAnswers(Map<String, Character> rawAnswers) {
        Map<UUID, Character> validatedAnswers = new HashMap<>();
        for (Map.Entry<String, Character> entry : rawAnswers.entrySet()) {
            try {
                UUID questionId = UUID.fromString(entry.getKey());
                validatedAnswers.put(questionId, entry.getValue());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid question ID format: " + entry.getKey());
            }
        }
        return validatedAnswers;
    }

    @Transactional
    public ScoreResponse createScore(CreateScoreRequest request, UserDetails userDetails) {
        // Get user
        User user = userRepository.findById(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Get quiz
        Quizz quizz = quizzRepository.findById(request.getQuizzId().toString())
                .orElseThrow(() -> new NotFoundException("Quiz not found"));

        // Check if student has already taken this quiz
        if (hasStudentTakenQuiz(request.getQuizzId(), UUID.fromString(userDetails.getUsername()))) {
            throw new BadRequestException("You have already taken this quiz");
        }

        // Get associated course
        Course course = quizz.getCourse() != null
                ? quizz.getCourse()
                : quizz.getChapter().getCourse();

        // Check if user has purchased the course
        if (!hasUserPurchasedCourse(userDetails.getUsername(), course.getId())) {
            throw new BadRequestException("You must purchase this course before taking its quizzes");
        }

        // Validate and convert answers
        Map<UUID, Character> validatedAnswers = validateAnswers(request.getAnswers());

        // Calculate score
        int score = calculateScore(validatedAnswers, quizz);

        // Create and save score entity
        Score scoreEntity = Score.builder()
                .user(user)
                .quizz(quizz)
                .score(score)
                .createdDate(new Date())
                .build();

        return modelMapper.map(scoreRepository.save(scoreEntity), ScoreResponse.class);
    }

    private int calculateScore(Map<UUID, Character> answers, Quizz quizz) {
        List<Question> questions = questionRepository.findByQuizz(quizz);

        if (questions.isEmpty()) {
            throw new BadRequestException("No questions found for this quiz");
        }

        int totalQuestions = questions.size();
        int correctAnswers = 0;

        for (Question question : questions) {
            UUID questionId = UUID.fromString(question.getId());
            Character studentAnswer = answers.get(questionId);

            if (studentAnswer != null
                    && Character.toLowerCase(studentAnswer) == Character.toLowerCase(question.getCorrect())) {
                correctAnswers++;
            }
        }

        return (int) Math.round((double) correctAnswers / totalQuestions * 100);
    }

    @Transactional
    public List<ScoreResponse> getAllScores(UUID quizzId) {
        Quizz quizz = quizzRepository.findById(quizzId.toString())
                .orElseThrow(() -> new NotFoundException("Quiz not found"));

        List<Score> scores = scoreRepository.findAll(
                (root, query, cb) -> cb.equal(root.get("quizz"), quizz),
                Sort.by(Sort.Direction.DESC, "createdDate")
        );

        return modelMapper.map(scores, new TypeToken<List<ScoreResponse>>() {
        }.getType());
    }

    @Transactional
    public ScoreResponse getStudentScore(UUID quizzId, UUID userId) {
        Quizz quizz = quizzRepository.findById(quizzId.toString())
                .orElseThrow(() -> new NotFoundException("Quiz not found"));

        User user = userRepository.findById(userId.toString())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Score score = scoreRepository.findOne(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("quizz"), quizz),
                        cb.equal(root.get("user"), user)
                )
        ).orElseThrow(() -> new NotFoundException("Score not found"));

        return modelMapper.map(score, ScoreResponse.class);
    }
}
