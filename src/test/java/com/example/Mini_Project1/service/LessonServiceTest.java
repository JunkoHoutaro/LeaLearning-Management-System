package com.example.Mini_Project1.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Lesson;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.exception.BadRequestException;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.repository.ChapterRepository;
import com.example.Mini_Project1.repository.LessonRepository;
import com.example.Mini_Project1.request.lesson.CreateLessonRequest;
import com.example.Mini_Project1.request.lesson.UpdateLessonRequest;
import com.example.Mini_Project1.response.lesson.LessonResponse;

class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private LessonService lessonService;

    private UUID chapterId;
    private UUID lessonId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        chapterId = UUID.randomUUID();
        lessonId = UUID.randomUUID();

    }

    @Test
    void createLesson_Success() throws Exception {
        CreateLessonRequest request = new CreateLessonRequest(chapterId, 1, "New Lesson", 1);
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
        when(modelMapper.map(request, Lesson.class)).thenReturn(lesson);
        when(modelMapper.map(lesson, LessonResponse.class)).thenReturn(new LessonResponse(
                lessonId.toString(),
                "Mock Lesson",
                1,
                null,
                null,
                lesson.getCreatedDate(),
                lesson.getUpdatedDate(),
                1,
                chapterId.toString()
        ));

        LessonResponse response = lessonService.createLesson(request, userDetails);

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
    void updateLesson_Success() throws Exception {
        UpdateLessonRequest request = new UpdateLessonRequest();
        request.setLessonId(lessonId);
        request.setName("Updated Lesson");
        request.setIndex(1);
        Chapter chapter = new Chapter();
        chapter.setId(chapterId.toString());
        Course course = new Course();
        User instructor = new User();
        instructor.setId("instructorId");
        course.setUser(instructor);
        chapter.setCourse(course);
        Lesson existingLesson = new Lesson();
        existingLesson.setId(lessonId.toString());
        existingLesson.setName("Old Lesson");
        existingLesson.setChapter(chapter);

        when(lessonRepository.findById(lessonId.toString())).thenReturn(Optional.of(existingLesson));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(existingLesson);
        when(userDetails.getUsername()).thenReturn("instructorId");

        LessonResponse response = lessonService.updateLesson(request, userDetails);

        assertEquals("Updated Lesson", response.getName());
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    void deleteLesson_Success() throws Exception {
        Lesson lesson = new Lesson();
        lesson.setId(lessonId.toString());
        lesson.setName("Lesson to delete");
        Chapter chapter = new Chapter();
        chapter.setId(chapterId.toString());
        Course course = new Course();
        User instructor = new User();
        instructor.setId("instructorId");
        course.setUser(instructor);
        chapter.setCourse(course);
        lesson.setChapter(chapter);

        when(lessonRepository.findById(lessonId.toString())).thenReturn(Optional.of(lesson));
        when(userDetails.getUsername()).thenReturn("instructorId");
        when(modelMapper.map(lesson, LessonResponse.class)).thenReturn(new LessonResponse(
                lessonId.toString(),
                "Lesson to delete",
                1,
                null,
                null,
                lesson.getCreatedDate(),
                lesson.getUpdatedDate(),
                1,
                chapterId.toString()
        ));

        LessonResponse response = lessonService.deleteLesson(lessonId, userDetails);

        assertEquals("Lesson to delete", response.getName());
        verify(lessonRepository, times(1)).delete(any(Lesson.class));
    }

    @Test
    void createLesson_ChapterNotFound() {
        UUID invalidChapterId = UUID.randomUUID();
        CreateLessonRequest request = new CreateLessonRequest(invalidChapterId, 1, "New Lesson", 1);
        when(chapterRepository.findById(invalidChapterId.toString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            lessonService.createLesson(request, userDetails);
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
        when(lessonRepository.findById(lessonId.toString())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> {
            lessonService.updateLesson(request, userDetails);
        });
        assertEquals("Lesson not found with ID: " + lessonId.toString(), exception.getMessage());
    }

    @Test
    void deleteLesson_LessonNotFound() {
        when(lessonRepository.findById(lessonId.toString())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> {
            lessonService.deleteLesson(lessonId, userDetails);
        });
        assertEquals("Lesson not found with ID: " + lessonId.toString(), exception.getMessage());
    }

    @Test
    void createLesson_DatabaseError() {
        CreateLessonRequest request = new CreateLessonRequest(chapterId, 1, "New Lesson", 1);
        Chapter chapter = new Chapter();
        chapter.setId(chapterId.toString());
        when(chapterRepository.findById(chapterId.toString())).thenReturn(Optional.of(chapter));
        when(lessonRepository.save(any(Lesson.class))).thenThrow(new RuntimeException("Database error"));
        Exception exception = assertThrows(RuntimeException.class, () -> {
            lessonService.createLesson(request, userDetails);
        });
        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void updateLesson_InvalidData() {
        UpdateLessonRequest request = new UpdateLessonRequest();
        request.setLessonId(UUID.randomUUID());
        request.setName("Updated Lesson");
        Exception exception = assertThrows(NotFoundException.class, () -> {
            lessonService.updateLesson(request, userDetails);
        });
        assertEquals("Lesson not found with ID: " + request.getLessonId(), exception.getMessage());
    }

    @Test
    void createLesson_InvalidData() {
        CreateLessonRequest request = new CreateLessonRequest(chapterId, 1, "", 1);
        Exception exception = assertThrows(BadRequestException.class, () -> {
            lessonService.createLesson(request, userDetails);
        });
        assertEquals("Lesson's name cannot be empty", exception.getMessage());
    }

    @Test
    void createLesson_InternalError() {
        CreateLessonRequest request = new CreateLessonRequest(chapterId, 1, "New Lesson", 1);
        Chapter chapter = new Chapter();
        chapter.setId(chapterId.toString());
        when(chapterRepository.findById(chapterId.toString())).thenReturn(Optional.of(chapter));
        when(lessonRepository.save(any(Lesson.class))).thenThrow(new RuntimeException("Internal error"));
        Exception exception = assertThrows(RuntimeException.class, () -> {
            lessonService.createLesson(request, userDetails);
        });
        assertEquals("Internal error", exception.getMessage());
    }

    @Test
    void createLesson_NameEmpty() {
        CreateLessonRequest request = new CreateLessonRequest(chapterId, 1, "", 1);
        Exception exception = assertThrows(BadRequestException.class, () -> {
            lessonService.createLesson(request, userDetails);
        });
        assertEquals("Lesson's name cannot be empty", exception.getMessage());
    }

    @Test
    void createLesson_EmptyName() {
        CreateLessonRequest request = new CreateLessonRequest(chapterId, 1, "", 1);
        Exception exception = assertThrows(BadRequestException.class, () -> {
            lessonService.createLesson(request, userDetails);
        });
        assertEquals("Lesson's name cannot be empty", exception.getMessage());
    }

    @Test
    void createLesson_ChapterNotFound_AdditionalCheck() {
        UUID invalidChapterId = UUID.randomUUID();
        CreateLessonRequest request = new CreateLessonRequest(invalidChapterId, 1, "New Lesson", 1);
        when(chapterRepository.findById(invalidChapterId.toString())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> {
            lessonService.createLesson(request, userDetails);
        });
        assertEquals("Chapter not found with ID: " + invalidChapterId.toString(), exception.getMessage());
        verify(chapterRepository, times(1)).findById(invalidChapterId.toString());
    }
}
