package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Lesson;
import com.example.Mini_Project1.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    public Optional<Lesson> getLessonById(String id) {
        return lessonRepository.findById(id);
    }

    public Lesson createLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    public Lesson updateLesson(String id, Lesson lessonDetails) {
        if (lessonRepository.existsById(id)) {
            lessonDetails.setId(id);
            return lessonRepository.save(lessonDetails);
        }
        return null;
    }

    public boolean deleteLesson(String id) {
        if (lessonRepository.existsById(id)) {
            lessonRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
