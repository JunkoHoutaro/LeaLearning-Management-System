package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.request.chapter.CreateChapterRequest;
import com.example.Mini_Project1.request.chapter.UpdateChapterRequest;
import com.example.Mini_Project1.response.chapter.ChapterResponse;
import com.example.Mini_Project1.service.ChapterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChapterControllerTest {

    @Mock
    private ChapterService chapterService;

    @InjectMocks
    private ChapterController chapterController;

    private UUID courseId;
    private UUID chapterId;
    private ChapterResponse mockChapterResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        courseId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        chapterId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");

        mockChapterResponse = new ChapterResponse();
        mockChapterResponse.setId(chapterId.toString());
        mockChapterResponse.setName("Mock Chapter");
        mockChapterResponse.setCreatedDate(new Date());
        mockChapterResponse.setUpdatedDate(new Date());
        mockChapterResponse.setCourseId(courseId.toString());
    }

    @Test
    void createChapter_Success() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1,"New Chapter");

        when(chapterService.createChapter(request)).thenReturn(mockChapterResponse);

        ResponseEntity<ChapterResponse> response = chapterController.createNewChapter(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockChapterResponse, response.getBody());
        verify(chapterService, times(1)).createChapter(request);
    }

    @Test
    void getChapters_Success() {
        List<ChapterResponse> mockChapterList = new ArrayList<>();
        mockChapterList.add(mockChapterResponse);

        when(chapterService.getChaptersByCourse(courseId)).thenReturn(mockChapterList);

        ResponseEntity<List<ChapterResponse>> response = chapterController.getChaptersByCourse(courseId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockChapterList, response.getBody());
        verify(chapterService, times(1)).getChaptersByCourse(courseId);
    }

    @Test
    void updateChapter_Success() {
        UpdateChapterRequest request = new UpdateChapterRequest();
        request.setChapterId(chapterId);
        request.setName("Updated Chapter");

        when(chapterService.updateChapter(request)).thenReturn(mockChapterResponse);

        ResponseEntity<ChapterResponse> response = chapterController.updateChapter(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockChapterResponse, response.getBody());
        verify(chapterService, times(1)).updateChapter(request);
    }

    @Test
    void deleteChapter_Success() {
        when(chapterService.deleteChapter(chapterId)).thenReturn(mockChapterResponse);

        ResponseEntity<ChapterResponse> response = chapterController.deleteChapter(chapterId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockChapterResponse, response.getBody());
        verify(chapterService, times(1)).deleteChapter(chapterId);
    }

    @Test
    void createChapter_InvalidCourseId() {
        CreateChapterRequest request = new CreateChapterRequest(null, 1,"Invalid Chapter");

        when(chapterService.createChapter(request)).thenThrow(new IllegalArgumentException("Course ID cannot be null"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chapterController.createNewChapter(request);
        });

        assertEquals("Course ID cannot be null", exception.getMessage());
    }

    @Test
    void updateChapter_ChapterNotFound() {
        UpdateChapterRequest request = new UpdateChapterRequest();
        request.setChapterId(UUID.randomUUID()); // Invalid chapter ID
        request.setName("Updated Chapter");

        when(chapterService.updateChapter(request)).thenThrow(new RuntimeException("Chapter not found"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            chapterController.updateChapter(request);
        });

        assertEquals("Chapter not found", exception.getMessage());
    }

    @Test
    void getChapters_NoChaptersFound() {
        when(chapterService.getChaptersByCourse(courseId)).thenReturn(new ArrayList<>());

        ResponseEntity<List<ChapterResponse>> response = chapterController.getChaptersByCourse(courseId);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isEmpty());
        verify(chapterService, times(1)).getChaptersByCourse(courseId);
    }

    @Test
    void createChapter_MissingName() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1,"");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chapterController.createNewChapter(request);
        });

        assertEquals("Chapter name cannot be empty", exception.getMessage());
    }

    @Test
    void createChapter_ServiceError() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1,"New Chapter");

        when(chapterService.createChapter(request)).thenThrow(new RuntimeException("Internal Server Error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            chapterController.createNewChapter(request);
        });

        assertEquals("Internal Server Error", exception.getMessage());
    }

    @Test
    void createChapter_ChapterAlreadyExists() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1,"Existing Chapter");

        when(chapterService.createChapter(request)).thenThrow(new IllegalArgumentException("Chapter already exists"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chapterController.createNewChapter(request);
        });

        assertEquals("Chapter already exists", exception.getMessage());
    }

    @Test
    void createChapter_Unauthorized() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1,"New Chapter");

        when(chapterService.createChapter(request)).thenThrow(new SecurityException("Unauthorized"));

        Exception exception = assertThrows(SecurityException.class, () -> {
            chapterController.createNewChapter(request);
        });

        assertEquals("Unauthorized", exception.getMessage());
    }

}
