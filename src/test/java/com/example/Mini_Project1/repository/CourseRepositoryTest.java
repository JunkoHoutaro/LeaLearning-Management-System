package com.example.Mini_Project1.repository;

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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CourseRepositoryTest {
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    UserRepository userRepository;

    private Course course;
    private User user;

    // Create a Course object for testing
    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("Jame")
                .email("jame@gmail.com")
                .build();

        course = Course.builder()
                .user(user)
                .name("Java Programming")
                .createdDate(new Date())
                .updatedDate(new Date())
                .price(200f)
                .discount(0.3f)
                .status(3)
                .build();

        user = userRepository.save(user);
        course = courseRepository.save(course);
    }

    // Clean up repository after each test
    @AfterEach
    void tearDown() {
        courseRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testSaveCourse() {
        Course mockCourse = Course.builder()
                            .name("Math")
                            .build();

        Course savedCourse = courseRepository.save(mockCourse);

        assertThat(savedCourse).isNotNull();
        assertThat(savedCourse.getId()).isNotNull();
        assertThat(savedCourse.getName()).isEqualTo("Math");
    }

    @Test
    void testFindCourseById() {
        Optional<Course> foundCourse = courseRepository.findById(course.getId());
        assertThat(foundCourse).isPresent();
    }

    @Test
    void testDeleteCourse() {
        courseRepository.delete(course);
        Optional<Course> deletedCourse = courseRepository.findById(course.getId());
        assertThat(deletedCourse).isNotPresent();
    }

    @Test
    void testFindCourseByStatus(){
        Course mockCourse = Course.builder()
                            .name("Math")
                            .status(1)
                            .build();

        courseRepository.save(mockCourse);

        List<Course> foundCourses = courseRepository.findCourseByStatus(3);
        assertThat(foundCourses).hasSize(1);
    }

    @Test
    void testFindCourseByNameContainingIgnoreCase(){
        Course mockCourse = Course.builder()
                .name("java programming")
                .build();

        courseRepository.save(mockCourse);

        List<Course> foundCourses = courseRepository.findCourseByNameContainingIgnoreCase("java programming");
        assertThat(foundCourses).hasSize(2);
    }

    @Test
    void testFindCourseByNameContainingIgnoreCaseAndStatus(){
        Course mockCourse = Course.builder()
                .name("java programming")
                .status(1)
                .build();

        courseRepository.save(mockCourse);

        List<Course> foundCourses = courseRepository.findCourseByNameContainingIgnoreCaseAndStatus("java", 3);
        assertThat(foundCourses).hasSize(1);
    }

    @Test
    void testFindCourseByUser(){
        List<Course> foundCourses = courseRepository.findCourseByUser(user);
        assertThat(foundCourses).hasSize(1);
    }

    @Test
    void testFindCourseByUserAndStatus(){
        Course mockCourse = Course.builder()
                            .user(user)
                            .status(1)
                            .build();

        courseRepository.save(mockCourse);

        List<Course> foundCourses = courseRepository.findCourseByUserAndStatus(user, 3);
        assertThat(foundCourses).hasSize(1);
    }

    @Test
    void testExistsByNameAndUser(){
        boolean exists = courseRepository.existsByNameAndUser("Math",user);
        assertThat(exists).isFalse();
    }
}
