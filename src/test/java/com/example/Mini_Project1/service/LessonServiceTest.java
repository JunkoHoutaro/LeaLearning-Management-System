package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Lesson;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.repository.LessonRepository;
import com.example.Mini_Project1.repository.ChapterRepository;
import com.example.Mini_Project1.request.lesson.CreateLessonRequest;
import com.example.Mini_Project1.request.lesson.UpdateLessonRequest;
import com.example.Mini_Project1.response.lesson.LessonResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private ChapterRepository chapterRepository;

    @InjectMocks
    private LessonService lessonService;

    private UUID chapterId;
    private UUID lessonId;
    private LessonResponse mockLessonResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        chapterId = UUID.fromString("1690e345-efea-4979-bde5-fdc47ba863b5");
        lessonId = UUID.fromString("130c711d-ec50-4576-b12b-a99786b6f338");

        mockLessonResponse = new LessonResponse();
        mockLessonResponse.setId(lessonId.toString());
        mockLessonResponse.setName("Mock Lesson");
        mockLessonResponse.setCreatedDate(new Date());
        mockLessonResponse.setUpdatedDate(new Date());
        mockLessonResponse.setChapterId(chapterId.toString());
    }

    @Test
    void createLesson_Success() {
        CreateLessonRequest request = new CreateLessonRequest(chapterId, 1, "New Lesson", "resourceUrl", "videoUrl", 1);

        Chapter chapter = new Chapter();
        chapter.setId(chapterId.toString());

        Lesson lesson = new Lesson();
        lesson.setId(lessonId.toString());
        lesson.setChapter(chapter);
        lesson.setCreatedDate(new Date());
        lesson.setUpdatedDate(new Date());
        lesson.setName("Mock Lesson");

        when(chapterRepository.findById(chapterId.toString())).thenReturn(Optional.of(chapter));

        when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);

        LessonResponse response = lessonService.createLesson(request);

        assertEquals("Mock Lesson", response.getName());
        assertNotNull(response.getId());
        assertEquals(chapterId.toString(), response.getChapterId());
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    void getLessons_Success() {
        Chapter chapter = new Chapter();
        chapter.setId(chapterId.toString());

        Lesson lesson = new Lesson();
        lesson.setId(lessonId.toString());
        lesson.setChapter(chapter);

        when(chapterRepository.findById(chapterId.toString())).thenReturn(java.util.Optional.of(chapter));
        when(lessonRepository.findByChapter(chapter)).thenReturn(List.of(lesson));

        List<LessonResponse> response = lessonService.getLessonsByChapter(chapterId);

        assertEquals(1, response.size());
        verify(lessonRepository, times(1)).findByChapter(chapter);
    }

    @Test
    void updateLesson_Success() {
        UpdateLessonRequest request = new UpdateLessonRequest();
        request.setLessonId(lessonId);
        request.setName("Updated Lesson");
        request.setIndex(1);

        Chapter chapter = new Chapter();
        chapter.setId(chapterId.toString());

        Lesson existingLesson = new Lesson();
        existingLesson.setId(lessonId.toString());
        existingLesson.setName("Old Lesson");
        existingLesson.setChapter(chapter);

        when(lessonRepository.findById(lessonId.toString())).thenReturn(Optional.of(existingLesson));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(existingLesson);

        LessonResponse response = lessonService.updateLesson(request);

        assertEquals("Updated Lesson", response.getName());
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    void deleteLesson_Success() {
        Lesson lesson = new Lesson();
        lesson.setId(lessonId.toString());
        lesson.setName("Lesson to delete");

        when(lessonRepository.findById(lessonId.toString())).thenReturn(java.util.Optional.of(lesson));

        LessonResponse response = lessonService.deleteLesson(lessonId);

        assertEquals("Lesson to delete", response.getName());
        verify(lessonRepository, times(1)).delete(any(Lesson.class));
    }

    @Test
    void createLesson_ChapterNotFound() {
        UUID invalidChapterId = UUID.randomUUID();
        CreateLessonRequest request = new CreateLessonRequest(invalidChapterId, 1, "New Lesson", "resourceUrl",
                "videoUrl",
                1);

        when(chapterRepository.findById(invalidChapterId.toString())).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            lessonService.createLesson(request);
        });

        assertEquals("Chapter not found with ID: " + invalidChapterId.toString(), exception.getMessage());
    }

    @Test
    void getLessons_ChapterNotFound() {
        UUID invalidChapterId = UUID.randomUUID();

        when(chapterRepository.findById(invalidChapterId.toString())).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            lessonService.getLessonsByChapter(invalidChapterId);
        });

        assertEquals("Chapter not found with ID: " + invalidChapterId.toString(), exception.getMessage());
    }

    @Test
    void updateLesson_LessonNotFound() {
        UpdateLessonRequest request = new UpdateLessonRequest();
        request.setLessonId(lessonId);
        request.setName("Updated Lesson");

        when(lessonRepository.findById(lessonId.toString())).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            lessonService.updateLesson(request);
        });

        assertEquals("Lesson not found with ID: " + lessonId.toString(), exception.getMessage());
    }

    @Test
    void deleteLesson_LessonNotFound() {
        when(lessonRepository.findById(lessonId.toString())).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            lessonService.deleteLesson(lessonId);
        });

        assertEquals("Lesson not found with ID: " + lessonId.toString(), exception.getMessage());
    }

    @Test
    void createLesson_DatabaseError() {
        CreateLessonRequest request = new CreateLessonRequest(chapterId, 1, "New Lesson", "resourceUrl", "videoUrl", 1);

        Chapter chapter = new Chapter();
        chapter.setId(chapterId.toString());

        when(chapterRepository.findById(chapterId.toString())).thenReturn(java.util.Optional.of(chapter));
        when(lessonRepository.save(any(Lesson.class))).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            lessonService.createLesson(request);
        });

        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void updateLesson_InvalidData() {
        UpdateLessonRequest request = new UpdateLessonRequest();
        request.setLessonId(UUID.randomUUID());
        request.setName("Updated Lesson");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            lessonService.updateLesson(request);
        });

        assertEquals("Lesson not found with ID: " + request.getLessonId(), exception.getMessage());
    }

    @Test
    void createLesson_InvalidData() {
        CreateLessonRequest request = new CreateLessonRequest(chapterId, 1, "", "resourceUrl", "videoUrl", 1);
        Exception exception = assertThrows(NotFoundException.class, () -> {
            lessonService.createLesson(request);
        });

        assertEquals("Chapter not found with ID: " + chapterId.toString(), exception.getMessage());
    }

    @Test
    void createLesson_InternalError() {
        CreateLessonRequest request = new CreateLessonRequest(chapterId, 1, "New Lesson", "resourceUrl", "videoUrl", 1);

        Chapter chapter = new Chapter();
        chapter.setId(chapterId.toString());

        when(chapterRepository.findById(chapterId.toString())).thenReturn(java.util.Optional.of(chapter));
        when(lessonRepository.save(any(Lesson.class))).thenThrow(new RuntimeException("Internal error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            lessonService.createLesson(request);
        });

        assertEquals("Internal error", exception.getMessage());
    }

    @Test
    void createLesson_NameEmpty() {
        CreateLessonRequest request = new CreateLessonRequest(chapterId, 1, "", "resourceUrl", "videoUrl", 1);
        Exception exception = assertThrows(NotFoundException.class, () -> {
            lessonService.createLesson(request);
        });

        assertEquals("Chapter not found with ID: " + chapterId.toString(), exception.getMessage());
    }

    @Test
    void createLesson_EmptyName() {
        CreateLessonRequest request = new CreateLessonRequest(chapterId, 1, "", "resourceUrl", "videoUrl", 1);
        Exception exception = assertThrows(NotFoundException.class, () -> {
            lessonService.createLesson(request);
        });

        assertEquals("Chapter not found with ID: " + chapterId.toString(), exception.getMessage());
    }

    @Test
    void createLesson_ChapterNotFound_AdditionalCheck() {
        UUID invalidChapterId = UUID.randomUUID();
        CreateLessonRequest request = new CreateLessonRequest(invalidChapterId, 1, "New Lesson", "resourceUrl",
                "videoUrl", 1);

        when(chapterRepository.findById(invalidChapterId.toString())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> {
            lessonService.createLesson(request);
        });

        assertEquals("Chapter not found with ID: " + invalidChapterId.toString(), exception.getMessage());
        verify(chapterRepository, times(1)).findById(invalidChapterId.toString());
    }

}
