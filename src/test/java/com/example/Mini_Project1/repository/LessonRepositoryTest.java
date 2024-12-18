package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Lesson;
import com.example.Mini_Project1.service.LessonService;
import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.entity.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LessonRepositoryTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private ChapterRepository chapterRepository;

    @InjectMocks
    private LessonService lessonService;

    private UUID chapterId;
    private UUID courseId;
    private UUID lessonId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        chapterId = UUID.fromString("1690e345-efea-4979-bde5-fdc47ba863b5");
        courseId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        lessonId = UUID.fromString("130c711d-ec50-4576-b12b-a99786b6f338");
    }

    @Test
    void testFindLessonsByCourse() {
        Course course = new Course();
        course.setId(courseId.toString());
        course.setName("Java Programming");

        Chapter chapter = new Chapter();
        chapter.setId(chapterId.toString());
        chapter.setName("Chapter 1");
        chapter.setCourse(course);

        Lesson lesson = new Lesson();
        lesson.setId(lessonId.toString());
        lesson.setName("Java Basics");
        lesson.setChapter(chapter);

        when(lessonRepository.findByChapter(chapter)).thenReturn(List.of(lesson));

        List<Lesson> lessons = lessonRepository.findByChapter(chapter);

        assertNotNull(lessons);
        assertEquals(1, lessons.size());
        assertEquals("Java Basics", lessons.get(0).getName());
    }

    @Test
    void testFindLessonsByCourse_NotFound() {
        Course course = new Course();
        course.setId(courseId.toString());
        course.setName("Java Programming");

        Chapter chapter = new Chapter();
        chapter.setId(chapterId.toString());
        chapter.setName("Chapter 1");
        chapter.setCourse(course);

        when(lessonRepository.findByChapter(chapter)).thenReturn(List.of());
        List<Lesson> lessons = lessonRepository.findByChapter(chapter);
        assertNotNull(lessons);
        assertTrue(lessons.isEmpty());
    }

    @Test
    void testFindLessonsByNonExistentChapter() {
        Chapter chapter = new Chapter();
        chapter.setId(UUID.randomUUID().toString());
        chapter.setName("Non-existent Chapter");
        when(lessonRepository.findByChapter(chapter)).thenReturn(List.of());
        List<Lesson> lessons = lessonRepository.findByChapter(chapter);
        assertNotNull(lessons);
        assertTrue(lessons.isEmpty());
    }

    @Test
    void testFindLessonsWithMultipleLessonsInChapter() {
        Course course = new Course();
        course.setId(courseId.toString());
        course.setName("Java Programming");

        Chapter chapter = new Chapter();
        chapter.setId(chapterId.toString());
        chapter.setName("Chapter 1");
        chapter.setCourse(course);
        Lesson lesson1 = new Lesson();
        lesson1.setId(UUID.randomUUID().toString());
        lesson1.setName("Java Basics");
        lesson1.setChapter(chapter);

        Lesson lesson2 = new Lesson();
        lesson2.setId(UUID.randomUUID().toString());
        lesson2.setName("Advanced Java");
        lesson2.setChapter(chapter);

        when(lessonRepository.findByChapter(chapter)).thenReturn(List.of(lesson1, lesson2));
        List<Lesson> lessons = lessonRepository.findByChapter(chapter);

        assertNotNull(lessons);
        assertEquals(2, lessons.size());
        assertEquals("Java Basics", lessons.get(0).getName());
        assertEquals("Advanced Java", lessons.get(1).getName());
    }

    @Test
    void testFindLessonsByChapterWithEmptyLessons() {
        Course course = new Course();
        course.setId(courseId.toString());
        course.setName("Java Programming");

        Chapter chapter = new Chapter();
        chapter.setId(chapterId.toString());
        chapter.setName("Chapter 1");
        chapter.setCourse(course);

        when(lessonRepository.findByChapter(chapter)).thenReturn(List.of());
        List<Lesson> lessons = lessonRepository.findByChapter(chapter);
        assertNotNull(lessons);
        assertTrue(lessons.isEmpty());
    }

    @Test
    void testFindLessonsWithNullChapterAttributes() {
        Chapter chapter = new Chapter();
        chapter.setId(chapterId.toString());
        chapter.setName("Incomplete Chapter");
        when(lessonRepository.findByChapter(chapter)).thenReturn(List.of());

        List<Lesson> lessons = lessonRepository.findByChapter(chapter);
        assertNotNull(lessons);
        assertTrue(lessons.isEmpty());
    }

    @Test
    void testFindLessonsWithInvalidData() {
        Course course = new Course();
        course.setId(courseId.toString());
        course.setName("Java Programming");

        Chapter chapter = new Chapter();
        chapter.setId(chapterId.toString());
        chapter.setName("Chapter 1");
        chapter.setCourse(course);
        Lesson lesson = new Lesson();
        lesson.setId("");
        lesson.setName(null);
        when(lessonRepository.findByChapter(chapter)).thenReturn(List.of(lesson));

        List<Lesson> lessons = lessonRepository.findByChapter(chapter);
        assertNotNull(lessons);
        assertEquals(1, lessons.size());
        assertNull(lessons.get(0).getName());
    }

    @Test
    void testFindLessonsChapterWithoutCourse() {
        Chapter chapter = new Chapter();
        chapter.setId(chapterId.toString());
        chapter.setName("Chapter 1");

        Course course = new Course();
        course.setId(UUID.randomUUID().toString());
        course.setName("Unrelated Course");

        Lesson lesson = new Lesson();
        lesson.setId(lessonId.toString());
        lesson.setName("Lesson 1");
        lesson.setChapter(chapter);

        when(lessonRepository.findByChapter(chapter)).thenReturn(List.of(lesson));

        List<Lesson> lessons = lessonRepository.findByChapter(chapter);
        assertNotNull(lessons);
        assertEquals(1, lessons.size());
        assertEquals("Lesson 1", lessons.get(0).getName());
    }

}
