package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ChapterService {

    @Autowired
    private ChapterRepository chapterRepository;

    public List<Chapter> getAllChapters() {
        return chapterRepository.findAll();
    }

    public Optional<Chapter> getChapterById(String id) {
        return chapterRepository.findById(id);
    }

    public Chapter createChapter(Chapter chapter) {
        return chapterRepository.save(chapter);
    }

    public Chapter updateChapter(String id, Chapter chapterDetails) {
        if (chapterRepository.existsById(id)) {
            chapterDetails.setId(id);
            return chapterRepository.save(chapterDetails);
        }
        return null;
    }

    public boolean deleteChapter(String id) {
        if (chapterRepository.existsById(id)) {
            chapterRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
