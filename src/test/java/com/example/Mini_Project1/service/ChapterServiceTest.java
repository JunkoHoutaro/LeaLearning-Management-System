package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.exception.BadRequestException;
import com.example.Mini_Project1.exception.ChapterNotFoundException;
import com.example.Mini_Project1.exception.CourseNotFoundException;
import com.example.Mini_Project1.repository.ChapterRepository;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.request.chapter.CreateChapterRequest;
import com.example.Mini_Project1.request.chapter.UpdateChapterRequest;
import com.example.Mini_Project1.response.chapter.ChapterResponse;
import com.jayway.jsonpath.spi.mapper.MappingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ChapterServiceTest {

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ChapterService chapterService;

    private UUID courseId;
    private UUID chapterId;
    private Course mockCourse;
    private Chapter mockChapter;
    private ChapterResponse mockChapterResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        courseId = UUID.fromString("887d1424-48da-4ba9-ae08-d67832ed9759");
        chapterId = UUID.fromString("75a8a179-d6e7-4e4e-8bdc-cf7c7f3a7c18");

        mockCourse = new Course();
        mockCourse.setId(courseId.toString());
        mockCourse.setName("Test Course");

        mockChapter = new Chapter();
        mockChapter.setId(chapterId.toString());
        mockChapter.setName("Test Chapter");
        mockChapter.setCourse(mockCourse);
        mockChapter.setCreatedDate(new Date());
        mockChapter.setUpdatedDate(new Date());

        mockChapterResponse = new ChapterResponse();
        mockChapterResponse.setId(chapterId.toString());
        mockChapterResponse.setName("Test Chapter");
        mockChapterResponse.setCourseId(courseId.toString());
        mockChapterResponse.setCreatedDate(mockChapter.getCreatedDate());
        mockChapterResponse.setUpdatedDate(mockChapter.getUpdatedDate());
    }

    @Test
    void createChapter_Success() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1, "New Chapter");

        when(courseRepository.findById(courseId.toString())).thenReturn(Optional.of(mockCourse));
        when(modelMapper.map(request, Chapter.class)).thenReturn(mockChapter);
        when(chapterRepository.save(mockChapter)).thenReturn(mockChapter);
        when(modelMapper.map(mockChapter, ChapterResponse.class)).thenReturn(mockChapterResponse);

        ChapterResponse response = chapterService.createChapter(request);

        assertEquals(mockChapterResponse, response);
        verify(chapterRepository, times(1)).save(mockChapter);
    }

    @Test
    void getChaptersByCourse_Success() {
        List<Chapter> chapters = new ArrayList<>();
        chapters.add(mockChapter);

        when(courseRepository.findById(courseId.toString())).thenReturn(Optional.of(mockCourse));
        when(chapterRepository.findByCourse(mockCourse)).thenReturn(chapters);
        when(modelMapper.map(chapters, new org.modelmapper.TypeToken<List<ChapterResponse>>() {
        }.getType())).thenReturn(List.of(mockChapterResponse));

        List<ChapterResponse> response = chapterService.getChaptersByCourse(courseId);

        assertEquals(1, response.size());
        assertEquals(mockChapterResponse, response.get(0));
    }

    @Test
    void updateChapter_Success() {
        UpdateChapterRequest request = new UpdateChapterRequest();
        request.setChapterId(chapterId);
        request.setName("Updated Chapter");
        request.setIndex(1);

        when(chapterRepository.findById(chapterId.toString())).thenReturn(Optional.of(mockChapter));
        doAnswer(invocation -> {
            UpdateChapterRequest source = invocation.getArgument(0);
            Chapter destination = invocation.getArgument(1);
            destination.setName(source.getName());
            return null;
        }).when(modelMapper).map(any(UpdateChapterRequest.class), eq(mockChapter));
        when(chapterRepository.save(mockChapter)).thenReturn(mockChapter);
        when(modelMapper.map(mockChapter, ChapterResponse.class)).thenReturn(mockChapterResponse);

        ChapterResponse response = chapterService.updateChapter(request);

        assertEquals(mockChapterResponse, response);
        verify(chapterRepository, times(1)).save(mockChapter);
    }

    @Test
    void deleteChapter_Success() {
        when(chapterRepository.findById(chapterId.toString())).thenReturn(Optional.of(mockChapter));
        when(modelMapper.map(mockChapter, ChapterResponse.class)).thenReturn(mockChapterResponse);

        ChapterResponse response = chapterService.deleteChapter(chapterId);

        assertEquals(mockChapterResponse, response);
        verify(chapterRepository, times(1)).delete(mockChapter);
    }

    @Test
    void deleteChapter_NotFound() {
        when(chapterRepository.findById(chapterId.toString())).thenReturn(Optional.empty());

        ChapterNotFoundException exception = assertThrows(ChapterNotFoundException.class,
                () -> chapterService.deleteChapter(chapterId));

        assertEquals("Chapter not found with ID: " + chapterId, exception.getMessage());
    }

    @Test
    void createChapter_CourseNotFound() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1, "NewChapter");

        when(courseRepository.findById(courseId.toString())).thenReturn(Optional.empty());

        CourseNotFoundException exception = assertThrows(CourseNotFoundException.class,
                () -> chapterService.createChapter(request));

        assertEquals("Course not found with ID: " + courseId, exception.getMessage());
    }

    @Test
    void getChaptersByCourse_CourseNotFound() {
        when(courseRepository.findById(courseId.toString())).thenReturn(Optional.empty());

        CourseNotFoundException exception = assertThrows(CourseNotFoundException.class,
                () -> chapterService.getChaptersByCourse(courseId));

        assertEquals("Course not found with ID: " + courseId, exception.getMessage());
    }

    @Test
    void updateChapter_ChapterNotFound() {
        UpdateChapterRequest request = new UpdateChapterRequest();
        request.setChapterId(chapterId);
        request.setName("Updated Chapter");

        when(chapterRepository.findById(chapterId.toString())).thenReturn(Optional.empty());

        ChapterNotFoundException exception = assertThrows(ChapterNotFoundException.class,
                () -> chapterService.updateChapter(request));

        assertEquals("Chapter not found with ID: " + chapterId, exception.getMessage());
    }

    @Test
    void deleteChapter_ChapterNotFound() {
        when(chapterRepository.findById(chapterId.toString())).thenReturn(Optional.empty());

        ChapterNotFoundException exception = assertThrows(ChapterNotFoundException.class,
                () -> chapterService.deleteChapter(chapterId));

        assertEquals("Chapter not found with ID: " + chapterId, exception.getMessage());
    }

    @Test
    void createChapter_SaveFailure() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1, "New Chapter");

        when(courseRepository.findById(courseId.toString())).thenReturn(Optional.of(mockCourse));
        when(modelMapper.map(request, Chapter.class)).thenReturn(mockChapter);
        when(chapterRepository.save(mockChapter)).thenThrow(new RuntimeException("Save failed"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chapterService.createChapter(request));

        assertEquals("Save failed", exception.getMessage());
    }

    @Test
    void createChapter_InvalidIndex() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, -1, "New Chapter");

        when(courseRepository.findById(courseId.toString())).thenReturn(Optional.of(mockCourse));

        // Mock chapter creation and saving
        Chapter mockChapter = new Chapter();
        mockChapter.setCourse(mockCourse); // Ensure chapter is not null

        when(modelMapper.map(request, Chapter.class)).thenReturn(mockChapter);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> chapterService.createChapter(request));

        assertEquals("Chapter index must be greater than 0.", exception.getMessage());
    }

    @Test
    void createChapter_IndexConflict() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1, "New Chapter");

        when(courseRepository.findById(courseId.toString())).thenReturn(Optional.of(mockCourse));
        when(chapterRepository.existsByCourseIdAndIndex(courseId.toString(), 1)).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> chapterService.createChapter(request));

        assertEquals("This course already has chapter with index 1", exception.getMessage());
    }

    @Test
    void updateChapter_NameCannotBeEmpty() {
        UpdateChapterRequest request = new UpdateChapterRequest();
        request.setChapterId(chapterId);
        request.setName("");

        when(chapterRepository.findById(chapterId.toString())).thenReturn(Optional.of(mockChapter));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> chapterService.updateChapter(request));
        assertEquals("Name cannot be empty", exception.getMessage());
    }

    @Test
    void deleteChapter_ChapterDoesNotExist() {
        when(chapterRepository.findById(chapterId.toString())).thenReturn(Optional.empty());

        ChapterNotFoundException exception = assertThrows(ChapterNotFoundException.class,
                () -> chapterService.deleteChapter(chapterId));

        assertEquals("Chapter not found with ID: " + chapterId, exception.getMessage());
    }

    @Test
    void updateChapter_NoChange() {
        UpdateChapterRequest request = new UpdateChapterRequest();
        request.setChapterId(chapterId);
        request.setName("Test Chapter");
        request.setIndex(1);

        when(chapterRepository.findById(chapterId.toString())).thenReturn(Optional.of(mockChapter));
        doAnswer(invocation -> {
            UpdateChapterRequest source = invocation.getArgument(0);
            Chapter destination = invocation.getArgument(1);
            destination.setName(source.getName());
            return null;
        }).when(modelMapper).map(any(UpdateChapterRequest.class), eq(mockChapter));

        when(chapterRepository.save(mockChapter)).thenReturn(mockChapter);
        when(modelMapper.map(mockChapter, ChapterResponse.class)).thenReturn(mockChapterResponse);

        ChapterResponse response = chapterService.updateChapter(request);

        assertEquals(mockChapterResponse, response);
        verify(chapterRepository, times(1)).save(mockChapter);
    }

}