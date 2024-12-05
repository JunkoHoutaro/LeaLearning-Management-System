package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Question;
import com.example.Mini_Project1.entity.Quizz;
import com.example.Mini_Project1.repository.QuizzRepository;
//import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.request.QuizzAndQuestion.CreateQuizzRequest;
import com.example.Mini_Project1.request.QuizzAndQuestion.UpdateQuizzRequest;
import com.example.Mini_Project1.response.QuizzAndQuestion.QuizzResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

@Service
@RequiredArgsConstructor
public class QuizzService {

    private QuizzRepository quizzRepository;

    // create
    public QuizzResponse createQuizzService(CreateQuizzRequest request) {
//        Course course = CourseRepository.findById(request.getCourseId().toString()).orElseThrow
//                (()-> new RuntimeException("Can't find course with id " + request.getCourseId().toString()));

        if(quizzRepository.existsByName(request.getName().trim()))
            throw new RuntimeException("This quizz already exist.");
        else {
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setSkipNullEnabled(true);

            Quizz quizz = modelMapper.map(request, Quizz.class);
            quizz.setCreatedDate(new Date());
            quizz.setUpdatedDate(new Date());

            return modelMapper.map(quizzRepository.save(quizz), QuizzResponse.class);
        }
    }

    // read all
    public List<QuizzResponse> getAllQuizzesService() {
        List<Quizz> quizzes = quizzRepository.findAll();
        return new ModelMapper().map(quizzes, new TypeToken<List<QuizzResponse>>() {}.getType());
    }

    // read by name
    public List<QuizzResponse> getQuestionByNameService(String name) {
        if(name == null){
            List<Quizz> quizzes = quizzRepository.findAll();
            return new ModelMapper().map(quizzes, new TypeToken<List<QuizzResponse>>() {}.getType());
        }
        List<Quizz> quizzes = quizzRepository.findByName(name);
        return new ModelMapper().map(quizzes, new TypeToken<List<QuizzResponse>>() {}.getType());

    }

    // update
    public QuizzResponse updateQuizzService(UpdateQuizzRequest request) {
        Quizz quizz = quizzRepository.findById(request.getQuizzId().toString()).orElseThrow(
                () -> new RuntimeException("Can't find quizz with id " + request.getQuizzId().toString()));

        if (request.getName() != null && quizzRepository.existsByName(request.getName())) {
            throw new RuntimeException("This quizz has already exist.");
        }
        quizz.setCreatedDate(new Date());
        quizz.setUpdatedDate(new Date());

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(request, quizz);

        return modelMapper.map(quizzRepository.save(quizz), QuizzResponse.class);
    }

    // delete
    public QuizzResponse deleteQuizzService(String quizzId) {
        Quizz quizz = quizzRepository.findById(quizzId.toString()).orElseThrow(
                ()-> new RuntimeException("Can't find quizz with id " + quizzId));

        quizzRepository.deleteById(quizzId);
        return new ModelMapper().map(quizzRepository.save(quizz), QuizzResponse.class);
    }
}
