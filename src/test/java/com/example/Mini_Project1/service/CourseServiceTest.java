package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.exception.BadRequestException;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.repository.PaymentRepository;
import com.example.Mini_Project1.repository.UserRepository;
import com.example.Mini_Project1.request.course.CreateCourseRequest;
import com.example.Mini_Project1.response.course.CourseResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CourseService courseService;

    private User user;
    private Course course;
    private CreateCourseRequest createCourseRequest;
    private CourseResponse expectedResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("2cd97901-4ba5-4a45-8628-6b30108a6f35");
        user.setName("John");
        user.setEmail("john@gmail.com");

        createCourseRequest = new CreateCourseRequest(
                UUID.fromString("2cd97901-4ba5-4a45-8628-6b30108a6f35"),
                "Math",
                100f,
                0.3f
        );

        course = new Course();
        course.setName("Math");
        course.setPrice(100f);
        course.setDiscount(0.3f);
        course.setUser(user);
        course.setCreatedDate(new Date());
        course.setUpdatedDate(new Date());
        course.setStatus(1);

        expectedResponse = new CourseResponse();
        expectedResponse.setName("Math");
        expectedResponse.setPrice(100f);
        expectedResponse.setDiscount(0.3f);
        expectedResponse.setCreatedDate(course.getCreatedDate());
        expectedResponse.setUpdatedDate(course.getUpdatedDate());
        expectedResponse.setStatus(1);
        expectedResponse.setUserId(user.getId());
    }

    @Test
    public void testCreateCourse_success() {
        when(userRepository.findById(createCourseRequest.getUserId().toString())).thenReturn(Optional.of(user));
        when(courseRepository.existsByNameAndUser(createCourseRequest.getName(), user)).thenReturn(false);
        when(modelMapper.map(createCourseRequest, Course.class)).thenReturn(course);
        when(courseRepository.save(course)).thenReturn(course);
        when(modelMapper.map(course, CourseResponse.class)).thenReturn(expectedResponse);

        CourseResponse response = courseService.createCourse(createCourseRequest);

        assertNotNull(response);
        assertEquals("Math", response.getName());
    }

    @Test
    public void testCreateCourse_notFound() {
        when(userRepository.findById(createCourseRequest.getUserId().toString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            courseService.createCourse(createCourseRequest);
        });

        assertEquals("Can't find user with id " + createCourseRequest.getUserId(), exception.getMessage());
    }

    @Test
    public void testCreateCourse_badRequest() {
        when(userRepository.findById(createCourseRequest.getUserId().toString())).thenReturn(Optional.of(user));
        when(courseRepository.existsByNameAndUser(createCourseRequest.getName(), user)).thenReturn(true);

        Exception exception = assertThrows(BadRequestException.class, () -> {
            courseService.createCourse(createCourseRequest);
        });

        assertEquals("This instructor has created a course with the same name", exception.getMessage());
    }
}
