package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.request.lesson.CreateLessonRequest;
import com.example.Mini_Project1.request.lesson.UpdateLessonRequest;
import com.example.Mini_Project1.response.lesson.LessonResponse;
import com.example.Mini_Project1.service.LessonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LessonControllerTest {

    @Mock
    private LessonService lessonService;

    @InjectMocks
    private LessonController lessonController;

    private UUID chapterId;
    private UUID lessonId;
    private LessonResponse mockLessonResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        chapterId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        lessonId = UUID.fromString("987e6543-e21b-32d3-a456-426614174111");

        mockLessonResponse = new LessonResponse();
        mockLessonResponse.setId(lessonId.toString());
        mockLessonResponse.setName("Mock Lesson");
    }

    @Test
    void createLesson_Success() {
        CreateLessonRequest request = new CreateLessonRequest(chapterId, 1,"New Lesson", "resourceUrl", "videoUrl", 1);

        when(lessonService.createLesson(request)).thenReturn(mockLessonResponse);

        ResponseEntity<LessonResponse> response = lessonController.createNewLesson(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockLessonResponse, response.getBody());
        verify(lessonService, times(1)).createLesson(request);
    }

    @Test
    void getLessons_Success() {
        when(lessonService.getLessonsByChapter(chapterId)).thenReturn(List.of(mockLessonResponse));

        ResponseEntity<List<LessonResponse>> response = lessonController.getLessonsByChapter(chapterId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(List.of(mockLessonResponse), response.getBody());
        verify(lessonService, times(1)).getLessonsByChapter(chapterId);
    }

    @Test
    void updateLesson_Success() {
        UpdateLessonRequest request = new UpdateLessonRequest();
        request.setLessonId(lessonId);
        request.setName("Updated Lesson");

        when(lessonService.updateLesson(request)).thenReturn(mockLessonResponse);

        ResponseEntity<LessonResponse> response = lessonController.updateLesson(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockLessonResponse, response.getBody());
        verify(lessonService, times(1)).updateLesson(request);
    }

    @Test
    void deleteLesson_Success() {
        when(lessonService.deleteLesson(lessonId)).thenReturn(mockLessonResponse);

        ResponseEntity<LessonResponse> response = lessonController.deleteLesson(lessonId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockLessonResponse, response.getBody());
        verify(lessonService, times(1)).deleteLesson(lessonId);
    }

    @Test
    void createLesson_InvalidData() {
        CreateLessonRequest request = new CreateLessonRequest(null,1, "", "resourceUrl", "videoUrl", 1); // Dữ liệu không
                                                                                                       // hợp lệ

        when(lessonService.createLesson(request)).thenThrow(new IllegalArgumentException("Invalid data"));

        try {
            lessonController.createNewLesson(request);
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    void getLessons_NoLessonsFound() {
        when(lessonService.getLessonsByChapter(chapterId)).thenReturn(List.of()); // Không có bài học

        ResponseEntity<List<LessonResponse>> response = lessonController.getLessonsByChapter(chapterId);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isEmpty());
        verify(lessonService, times(1)).getLessonsByChapter(chapterId);
    }

    @Test
    void updateLesson_LessonNotFound() {
        UpdateLessonRequest request = new UpdateLessonRequest();
        request.setLessonId(UUID.randomUUID());
        request.setName("Updated Lesson");

        when(lessonService.updateLesson(request)).thenThrow(new RuntimeException("Lesson not found"));

        ResponseEntity<LessonResponse> response = lessonController.updateLesson(request);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void deleteLesson_LessonNotFound() {
        when(lessonService.deleteLesson(lessonId)).thenThrow(new RuntimeException("Lesson not found"));

        ResponseEntity<LessonResponse> response = lessonController.deleteLesson(lessonId);

        assertEquals(404, response.getStatusCode().value());
        verify(lessonService, times(1)).deleteLesson(lessonId);
    }

    @Test
    void getLessons_NoLessonsReturned() {
        when(lessonService.getLessonsByChapter(chapterId)).thenReturn(null);

        ResponseEntity<List<LessonResponse>> response = lessonController.getLessonsByChapter(chapterId);

        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void createLesson_ChapterNotFound() {
        CreateLessonRequest request = new CreateLessonRequest(UUID.randomUUID(), 1,"New Lesson", "resourceUrl",
                "videoUrl", 1);

        when(lessonService.createLesson(request)).thenThrow(new RuntimeException("Chapter not found"));

        ResponseEntity<LessonResponse> response = lessonController.createNewLesson(request);

        assertEquals(404, response.getStatusCode().value());
    }

}
