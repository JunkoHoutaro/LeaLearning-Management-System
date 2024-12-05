package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @GetMapping
    public List<Chapter> getAllChapters() {
        return chapterService.getAllChapters();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chapter> getChapterById(@PathVariable String id) {
        Optional<Chapter> chapter = chapterService.getChapterById(id);
        return chapter.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Chapter createChapter(@RequestBody Chapter chapter) {
        return chapterService.createChapter(chapter);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chapter> updateChapter(@PathVariable String id, @RequestBody Chapter chapterDetails) {
        Chapter updatedChapter = chapterService.updateChapter(id, chapterDetails);
        return updatedChapter != null ? ResponseEntity.ok(updatedChapter)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapter(@PathVariable String id) {
        return chapterService.deleteChapter(id) ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
