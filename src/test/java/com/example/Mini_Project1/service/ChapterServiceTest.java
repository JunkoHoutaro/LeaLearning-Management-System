package com.example.Mini_Project1.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.exception.AccessDeniedException;
import com.example.Mini_Project1.exception.BadRequestException;
import com.example.Mini_Project1.exception.ChapterNotFoundException;
import com.example.Mini_Project1.exception.CourseNotFoundException;
import com.example.Mini_Project1.repository.ChapterRepository;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.request.chapter.CreateChapterRequest;
import com.example.Mini_Project1.request.chapter.UpdateChapterRequest;
import com.example.Mini_Project1.response.chapter.ChapterResponse;

class ChapterServiceTest {

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserDetails userDetails;

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

        User mockUser = new User();
        mockUser.setId("testUser");

        mockCourse = new Course();
        mockCourse.setId(courseId.toString());
        mockCourse.setName("Test Course");
        mockCourse.setUser(mockUser);

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

        when(userDetails.getUsername()).thenReturn("testUser");
    }

    @Test
    void createChapter_AccessDenied() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1, "New Chapter");
        when(courseRepository.findById(courseId.toString())).thenReturn(Optional.of(mockCourse));
        when(userDetails.getUsername()).thenReturn("unauthorizedUser");

        assertThrows(AccessDeniedException.class, ()
                -> chapterService.createChapter(request, userDetails));
    }

    @Test
    void updateChapter_AccessDenied() {
        UpdateChapterRequest request = new UpdateChapterRequest();
        request.setChapterId(chapterId);
        when(chapterRepository.findById(chapterId.toString())).thenReturn(Optional.of(mockChapter));
        when(userDetails.getUsername()).thenReturn("unauthorizedUser");

        assertThrows(AccessDeniedException.class, ()
                -> chapterService.updateChapter(request, userDetails));
    }

    @Test
    void deleteChapter_AccessDenied() {
        when(chapterRepository.findById(chapterId.toString())).thenReturn(Optional.of(mockChapter));
        when(userDetails.getUsername()).thenReturn("unauthorizedUser");

        assertThrows(AccessDeniedException.class, ()
                -> chapterService.deleteChapter(chapterId, userDetails));
    }

    @Test
    void createChapter_Success() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1, "New Chapter");
        when(courseRepository.findById(courseId.toString())).thenReturn(Optional.of(mockCourse));
        when(modelMapper.map(request, Chapter.class)).thenReturn(mockChapter);
        when(chapterRepository.save(mockChapter)).thenReturn(mockChapter);
        when(modelMapper.map(mockChapter, ChapterResponse.class)).thenReturn(mockChapterResponse);

        ChapterResponse response = chapterService.createChapter(request, userDetails);

        assertEquals(mockChapterResponse, response);
        verify(chapterRepository, times(1)).save(mockChapter);
        verify(userDetails, times(1)).getUsername();
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
        when(chapterRepository.save(mockChapter)).thenReturn(mockChapter);
        when(modelMapper.map(mockChapter, ChapterResponse.class)).thenReturn(mockChapterResponse);

        ChapterResponse response = chapterService.updateChapter(request, userDetails);

        assertEquals(mockChapterResponse, response);
        verify(chapterRepository, times(1)).save(mockChapter);
    }

    @Test
    void deleteChapter_Success() {
        when(chapterRepository.findById(chapterId.toString())).thenReturn(Optional.of(mockChapter));
        when(modelMapper.map(mockChapter, ChapterResponse.class)).thenReturn(mockChapterResponse);

        ChapterResponse response = chapterService.deleteChapter(chapterId, userDetails);

        assertEquals(mockChapterResponse, response);
        verify(chapterRepository, times(1)).delete(mockChapter);
        verify(userDetails, times(1)).getUsername();
    }

    @Test
    void deleteChapter_NotFound() {
        when(chapterRepository.findById(chapterId.toString())).thenReturn(Optional.empty());

        ChapterNotFoundException exception = assertThrows(ChapterNotFoundException.class,
                () -> chapterService.deleteChapter(chapterId, userDetails));

        assertEquals("Chapter not found with ID: " + chapterId, exception.getMessage());
    }

    @Test
    void createChapter_CourseNotFound() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1, "NewChapter");
        when(courseRepository.findById(courseId.toString())).thenReturn(Optional.empty());

        CourseNotFoundException exception = assertThrows(CourseNotFoundException.class,
                () -> chapterService.createChapter(request, userDetails));

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
                () -> chapterService.updateChapter(request, userDetails));

        assertEquals("Chapter not found with ID: " + chapterId, exception.getMessage());
    }

    @Test
    void deleteChapter_ChapterNotFound() {
        when(chapterRepository.findById(chapterId.toString())).thenReturn(Optional.empty());

        ChapterNotFoundException exception = assertThrows(ChapterNotFoundException.class,
                () -> chapterService.deleteChapter(chapterId, userDetails));

        assertEquals("Chapter not found with ID: " + chapterId, exception.getMessage());
    }

    @Test
    void createChapter_SaveFailure() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1, "New Chapter");
        when(courseRepository.findById(courseId.toString())).thenReturn(Optional.of(mockCourse));
        when(modelMapper.map(request, Chapter.class)).thenReturn(mockChapter);
        when(chapterRepository.save(mockChapter)).thenThrow(new RuntimeException("Save failed"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> chapterService.createChapter(request, userDetails));

        assertEquals("Save failed", exception.getMessage());
    }

    @Test
    void createChapter_InvalidIndex() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, -1, "New Chapter");
        when(courseRepository.findById(courseId.toString())).thenReturn(Optional.of(mockCourse));
        when(userDetails.getUsername()).thenReturn("testUser");

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> chapterService.createChapter(request, userDetails));
        assertEquals("Chapter index must be greater than 0", exception.getMessage());

        verify(courseRepository).findById(courseId.toString());
        verify(userDetails).getUsername();
    }

    @Test
    void createChapter_IndexConflict() {
        CreateChapterRequest request = new CreateChapterRequest(courseId, 1, "New Chapter");
        when(courseRepository.findById(courseId.toString())).thenReturn(Optional.of(mockCourse));
        when(chapterRepository.existsByCourseIdAndIndex(courseId.toString(), 1)).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> chapterService.createChapter(request, userDetails));

        assertEquals("This course already has a chapter with index 1", exception.getMessage());
    }

    @Test
    void updateChapter_NameCannotBeEmpty() {
        UpdateChapterRequest request = new UpdateChapterRequest();
        request.setChapterId(chapterId);
        request.setName("");
        when(chapterRepository.findById(chapterId.toString())).thenReturn(Optional.of(mockChapter));

        assertThrows(BadRequestException.class, ()
                -> chapterService.updateChapter(request, userDetails));
    }

    @Test
    void deleteChapter_ChapterDoesNotExist() {
        when(chapterRepository.findById(chapterId.toString())).thenReturn(Optional.empty());

        ChapterNotFoundException exception = assertThrows(ChapterNotFoundException.class,
                () -> chapterService.deleteChapter(chapterId, userDetails));

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

        ChapterResponse response = chapterService.updateChapter(request, userDetails);

        assertEquals(mockChapterResponse, response);
        verify(chapterRepository, times(1)).save(mockChapter);
    }
}
