package com.example.Mini_Project1.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.example.Mini_Project1.request.chapter.CreateChapterRequest;
import com.example.Mini_Project1.request.chapter.UpdateChapterRequest;
import com.example.Mini_Project1.response.chapter.ChapterResponse;
import com.example.Mini_Project1.service.ChapterService;

class ChapterControllerTest {

    @Mock
    private ChapterService chapterService;

    @InjectMocks
    private ChapterController chapterController;

    @Mock
    private UserDetails userDetails;

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
    void createNewChapter_Success() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1, "New Chapter");
        ChapterResponse response = new ChapterResponse();
        when(chapterService.createChapter(request, userDetails)).thenReturn(response);

        ResponseEntity<ChapterResponse> result = chapterController.createNewChapter(request, userDetails);

        assertEquals(response, result.getBody());
        verify(chapterService, times(1)).createChapter(request, userDetails);
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
        request.setChapterId(UUID.randomUUID());
        request.setName("Updated Chapter");
        ChapterResponse response = new ChapterResponse();
        when(chapterService.updateChapter(request, userDetails)).thenReturn(response);

        ResponseEntity<ChapterResponse> result = chapterController.updateChapter(request, userDetails);

        assertEquals(response, result.getBody());
        verify(chapterService, times(1)).updateChapter(request, userDetails);
    }

    @Test
    void deleteChapter_Success() {
        UUID chapterId = UUID.randomUUID();
        ChapterResponse response = new ChapterResponse();
        when(chapterService.deleteChapter(chapterId, userDetails)).thenReturn(response);

        ResponseEntity<ChapterResponse> result = chapterController.deleteChapter(chapterId, userDetails);

        assertEquals(response, result.getBody());
        verify(chapterService, times(1)).deleteChapter(chapterId, userDetails);
    }

    @Test
    void deleteChapter_ChapterNotFound() {
        UUID chapterId = UUID.randomUUID();
        when(chapterService.deleteChapter(chapterId, userDetails)).thenThrow(new IllegalArgumentException("Chapter not found"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chapterController.deleteChapter(chapterId, userDetails);
        });

        assertEquals("Chapter not found", exception.getMessage());
    }

    @Test
    void createChapter_InvalidCourseId() {
        UUID invalidCourseId = UUID.randomUUID();
        CreateChapterRequest request = new CreateChapterRequest(invalidCourseId, 1, "New Chapter");
        when(chapterService.createChapter(request, userDetails)).thenThrow(new IllegalArgumentException("Invalid course ID"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chapterController.createNewChapter(request, userDetails);
        });

        assertEquals("Invalid course ID", exception.getMessage());
    }

    @Test
    void updateChapter_ChapterNotFound() {
        UpdateChapterRequest request = new UpdateChapterRequest();
        request.setChapterId(UUID.randomUUID());
        request.setName("Updated Chapter");
        when(chapterService.updateChapter(request, userDetails)).thenThrow(new IllegalArgumentException("Chapter not found"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chapterController.updateChapter(request, userDetails);
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
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1, "");
        when(chapterService.createChapter(request, userDetails)).thenThrow(new IllegalArgumentException("Chapter name cannot be empty"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chapterController.createNewChapter(request, userDetails);
        });

        assertEquals("Chapter name cannot be empty", exception.getMessage());
    }

    @Test
    void createChapter_ServiceError() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1, "New Chapter");
        when(chapterService.createChapter(request, userDetails)).thenThrow(new RuntimeException("Service error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            chapterController.createNewChapter(request, userDetails);
        });

        assertEquals("Service error", exception.getMessage());
    }

    @Test
    void createNewChapter_ChapterAlreadyExists() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1, "New Chapter");
        when(chapterService.createChapter(request, userDetails)).thenThrow(new IllegalArgumentException("Chapter already exists"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            chapterController.createNewChapter(request, userDetails);
        });

        assertEquals("Chapter already exists", exception.getMessage());
    }

    @Test
    void createChapter_Unauthorized() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1, "New Chapter");
        when(chapterService.createChapter(request, userDetails)).thenThrow(new SecurityException("Unauthorized"));

        Exception exception = assertThrows(SecurityException.class, () -> {
            chapterController.createNewChapter(request, userDetails);
        });

        assertEquals("Unauthorized", exception.getMessage());
    }
}
