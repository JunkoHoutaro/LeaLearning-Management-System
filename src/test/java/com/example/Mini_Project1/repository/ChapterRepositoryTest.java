package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Chapter;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ChapterRepositoryTest {

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private CourseRepository courseRepository;

    private Course course;
    private Chapter chapter;

    @BeforeEach
    void setUp() {
        course = Course.builder()
                .name("Java Programming")
                .createdDate(new Date())
                .updatedDate(new Date())
                .price(200f)
                .discount(0.3f)
                .status(3)
                .build();

        course = courseRepository.save(course);

        chapter = Chapter.builder()
                .name("Introduction to Java")
                .course(course)
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();

        chapter = chapterRepository.save(chapter);
    }

    @AfterEach
    void tearDown() {
        chapterRepository.deleteAll();
        courseRepository.deleteAll();
    }

    @Test
    void testSaveChapter() {
        Chapter mockChapter = Chapter.builder()
                .name("Advanced Java")
                .course(course)
                .build();

        Chapter savedChapter = chapterRepository.save(mockChapter);

        assertThat(savedChapter).isNotNull();
        assertThat(savedChapter.getId()).isNotNull();
        assertThat(savedChapter.getName()).isEqualTo("Advanced Java");
    }

    @Test
    void testFindChapterById() {
        Optional<Chapter> foundChapter = chapterRepository.findById(chapter.getId());
        assertThat(foundChapter).isPresent();
    }

    @Test
    void testDeleteChapter() {
        chapterRepository.delete(chapter);
        Optional<Chapter> deletedChapter = chapterRepository.findById(chapter.getId());
        assertThat(deletedChapter).isNotPresent();
    }

    @Test
    void testFindChaptersByCourse() {
        List<Chapter> foundChapters = chapterRepository.findByCourse(course);
        assertThat(foundChapters).hasSize(1);
    }

    @Test
    void testExistsByNameAndCourse() {
        boolean exists = chapterRepository.existsByNameAndCourse("Introduction to Java", course);
        assertThat(exists).isTrue();
    }

    @Test
    void testFindChapterByNameContainingIgnoreCase() {
        Course course = new Course();
        course.setName("Java Programming");
        course.setPrice(200f);
        course.setDiscount(0.3f);
        course.setStatus(3);
        course.setCreatedDate(new Date());
        course.setUpdatedDate(new Date());
        course = courseRepository.save(course);

        Chapter mockChapter = Chapter.builder()
                .name("Advanced Java")
                .course(course)
                .build();
        chapterRepository.save(mockChapter);

        List<Chapter> allChapters = chapterRepository.findAll();
        List<Chapter> filteredChapters = allChapters.stream()
                .filter(chapter -> chapter.getName().toLowerCase().contains("java"))
                .collect(Collectors.toList());
        assertThat(filteredChapters).hasSize(1);
        assertThat(filteredChapters)
                .extracting("name")
                .contains("Advanced Java");
    }

    @Test
    void testFindChapterByCourse() {
        Course mockCourse = Course.builder()
                .name("Spring Framework")
                .build();

        Course savedCourse = courseRepository.save(mockCourse);
        Chapter mockChapter = Chapter.builder()
                .name("Spring Basics")
                .course(savedCourse)
                .build();

        chapterRepository.save(mockChapter);
        List<Chapter> allChapters = chapterRepository.findAll();
        List<Chapter> filteredChapters = allChapters.stream()
                .filter(chapter -> chapter.getCourse().equals(savedCourse))
                .collect(Collectors.toList());
        assertThat(filteredChapters).hasSize(1);
        assertThat(filteredChapters)
                .extracting("name")
                .contains("Spring Basics");
    }

}
