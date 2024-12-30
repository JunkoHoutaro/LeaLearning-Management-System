package com.example.Mini_Project1.controller;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.Mini_Project1.request.lesson.CreateLessonRequest;
import com.example.Mini_Project1.request.lesson.UpdateLessonRequest;
import com.example.Mini_Project1.response.lesson.LessonResponse;
import com.example.Mini_Project1.service.LessonService;

class LessonControllerTest {

    @Mock
    private LessonService lessonService;

    @Mock
    private UserDetails userDetails;

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
    void createLesson_Success() throws Exception {
        CreateLessonRequest request = new CreateLessonRequest(chapterId, 1, "New Lesson", 1);
        LessonResponse lessonResponse = new LessonResponse();
        when(lessonService.createLesson(request, userDetails)).thenReturn(lessonResponse);

        ResponseEntity<LessonResponse> response = lessonController.createLesson(request, userDetails);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(lessonResponse, response.getBody());
        verify(lessonService, times(1)).createLesson(request, userDetails);
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
        when(lessonService.updateLesson(request, userDetails)).thenReturn(mockLessonResponse);
        ResponseEntity<LessonResponse> response = lessonController.updateLesson(request, userDetails);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockLessonResponse, response.getBody());
        verify(lessonService, times(1)).updateLesson(request, userDetails);
    }

    @Test
    void deleteLesson_Success() {
        when(lessonService.deleteLesson(lessonId, userDetails)).thenReturn(mockLessonResponse);
        ResponseEntity<LessonResponse> response = lessonController.deleteLesson(lessonId, userDetails);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockLessonResponse, response.getBody());
        verify(lessonService, times(1)).deleteLesson(lessonId, userDetails);
    }

    @Test
    void createLesson_InvalidData() throws Exception {
        CreateLessonRequest request = new CreateLessonRequest(null, 1, "", 1);
        when(lessonService.createLesson(request, userDetails)).thenThrow(new IllegalArgumentException("Invalid data"));
        try {
            lessonController.createLesson(request, userDetails);
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    void getLessons_NoLessonsFound() {
        when(lessonService.getLessonsByChapter(chapterId)).thenReturn(List.of());

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
        when(lessonService.updateLesson(request, userDetails)).thenThrow(new RuntimeException("Lesson not found"));
        ResponseEntity<LessonResponse> response = lessonController.updateLesson(request, userDetails);
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void deleteLesson_LessonNotFound() {
        when(lessonService.deleteLesson(lessonId, userDetails)).thenThrow(new RuntimeException("Lesson not found"));
        ResponseEntity<LessonResponse> response = lessonController.deleteLesson(lessonId, userDetails);
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void getLessons_NoLessonsReturned() {
        when(lessonService.getLessonsByChapter(chapterId)).thenReturn(null);
        ResponseEntity<List<LessonResponse>> response = lessonController.getLessonsByChapter(chapterId);
        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void createLesson_ChapterNotFound() throws Exception {
        CreateLessonRequest request = new CreateLessonRequest(UUID.randomUUID(), 1, "New Lesson", 1);
        when(lessonService.createLesson(request, userDetails)).thenThrow(new RuntimeException("Chapter not found"));
        ResponseEntity<LessonResponse> response = lessonController.createLesson(request, userDetails);
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}
