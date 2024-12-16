package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Payment;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.enums.CourseStatus;
import com.example.Mini_Project1.enums.PaymentStatus;
import com.example.Mini_Project1.exception.BadRequestException;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.repository.PaymentRepository;
import com.example.Mini_Project1.repository.UserRepository;
import com.example.Mini_Project1.request.course.CreateCourseRequest;
import com.example.Mini_Project1.response.course.CourseResponse;
import com.example.Mini_Project1.response.user.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.util.*;
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
    private Payment payment;
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

        payment = new Payment();
        payment.setStatus(3);
        payment.setCreatedDate(new Date());
        payment.setUpdatedDate(new Date());
        payment.setCourse(course);
        payment.setUser(user);
        payment.setPrice(100f);
        payment.setDiscount(0.1f);
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

    @Test
    public void testGetCoursesByStatus_whenStatusIsNull(){
        List<Course> mockCourses = new ArrayList<>();
        mockCourses.add(new Course());
        mockCourses.add(course);

        when(courseRepository.findAll()).thenReturn(mockCourses);

        List<CourseResponse> mockResponses = new ArrayList<>();
        mockResponses.add(new CourseResponse());
        mockResponses.add(expectedResponse);

        when(modelMapper.map(mockCourses, new TypeToken<List<CourseResponse>>(){}.getType())).thenReturn(mockResponses);

        List<CourseResponse> result = courseService.getCoursesByStatus(null);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetCoursesByStatus_whenStatusIsNotNull() {
        List<Course> mockCourses = new ArrayList<>();
        mockCourses.add(course);

        when(courseRepository.findCourseByStatus(1)).thenReturn(mockCourses);

        List<CourseResponse> mockResponse = new ArrayList<>();
        mockResponse.add(expectedResponse);

        when(modelMapper.map(mockCourses, new TypeToken<List<CourseResponse>>(){}.getType())).thenReturn(mockResponse);

        List<CourseResponse> result = courseService.getCoursesByStatus(CourseStatus.PENDING);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Math", result.getFirst().getName());
    }

    @Test
    public void testSearchCourses_whenStatusIsNull() {
        String name = "math";

        Course mockCourse = new Course();
        mockCourse.setName("math1");
        mockCourse.setPrice(180f);

        List<Course> mockCourses = new ArrayList<>();
        mockCourses.add(mockCourse);
        mockCourses.add(course);

        when(courseRepository.findCourseByNameContainingIgnoreCase(name)).thenReturn(mockCourses);

        CourseResponse mockResponse = new CourseResponse();
        mockResponse.setName("math1");
        mockResponse.setPrice(180f);

        List<CourseResponse> mockResponses = new ArrayList<>();
        mockResponses.add(expectedResponse);
        mockResponses.add(mockResponse);

        when(modelMapper.map(mockCourses, new TypeToken<List<CourseResponse>>() {}.getType())).thenReturn(mockResponses);

        List<CourseResponse> result = courseService.searchCourses(name, null, true);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Math", result.get(0).getName());
        assertEquals("math1", result.get(1).getName());
        assertTrue(result.get(0).getPrice() <= result.get(1).getPrice());
    }

    @Test
    public void testSearchCourses_whenStatusIsNotNull() {
        String name = "math";

        List<Course> mockCourses = Collections.singletonList(course);
        when(courseRepository.findCourseByNameContainingIgnoreCaseAndStatus(name,1)).thenReturn(mockCourses);

        List<CourseResponse> mockResponses = Collections.singletonList(expectedResponse);
        when(modelMapper.map(mockCourses, new TypeToken<List<CourseResponse>>() {}.getType())).thenReturn(mockResponses);

        List<CourseResponse> result = courseService.searchCourses(name, CourseStatus.PENDING, false);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Math", result.getFirst().getName());
    }

    @Test
    public void testGetPurchasedCourses_whenStatusIsNull() {
        List<Payment> mockPayments = Collections.singletonList(payment);
        List<Course> mockCourses = mockPayments.stream().map(Payment::getCourse).toList();
        List<CourseResponse> mockCourseResponses = Collections.singletonList(expectedResponse);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(paymentRepository.findByUser(user)).thenReturn(mockPayments);
        when(modelMapper.map(mockCourses, new TypeToken<List<CourseResponse>>() {}.getType())).thenReturn(mockCourseResponses);

        List<CourseResponse> result = courseService.getPurchasedCourses(UUID.fromString(user.getId()), null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Math", result.getFirst().getName());
    }

    @Test
    public void testGetPurchasedCourses_whenStatusIsNotNull() {
        List<Payment> mockPayments = Collections.singletonList(payment);
        List<Course> mockCourses = mockPayments.stream().map(Payment::getCourse).toList();
        List<CourseResponse> mockCourseResponses = Collections.singletonList(expectedResponse);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(paymentRepository.findByUserAndStatus(user,3)).thenReturn(mockPayments);
        when(modelMapper.map(mockCourses, new TypeToken<List<CourseResponse>>() {}.getType())).thenReturn(mockCourseResponses);

        List<CourseResponse> result = courseService.getPurchasedCourses(UUID.fromString(user.getId()), PaymentStatus.SUCCESS);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Math", result.getFirst().getName());
    }

    @Test
    public void testGetPurchasedCourses_notFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class, () -> courseService.getPurchasedCourses(UUID.fromString(user.getId()), null));
        assertEquals("Can't find user with id " + user.getId(), exception.getMessage());
    }

    @Test
    public void testGetCoursesByInstructor_whenStatusIsNull() {
        List<Course> mockCourses = List.of(course);
        List<CourseResponse> mockCourseResponses = List.of(expectedResponse);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(courseRepository.findCourseByUser(user)).thenReturn(mockCourses);
        when(modelMapper.map(mockCourses, new TypeToken<List<CourseResponse>>() {}.getType())).thenReturn(mockCourseResponses);

        List<CourseResponse> result = courseService.getCoursesByInstructor(UUID.fromString(user.getId()), null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Math", result.getFirst().getName());
    }

    @Test
    public void testGetCoursesByInstructor_whenStatusIsNotNull() {
        List<Course> mockCourses = List.of(course);
        List<CourseResponse> mockCourseResponses = List.of(expectedResponse);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(courseRepository.findCourseByUserAndStatus(user, 1)).thenReturn(mockCourses);
        when(modelMapper.map(mockCourses, new TypeToken<List<CourseResponse>>() {}.getType())).thenReturn(mockCourseResponses);

        List<CourseResponse> result = courseService.getCoursesByInstructor(UUID.fromString(user.getId()), CourseStatus.PENDING);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Math", result.getFirst().getName());
    }

    @Test
    public void testGetCoursesByInstructor_notFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class,
                () -> courseService.getCoursesByInstructor(UUID.fromString(user.getId()), null));
        assertEquals("Can't find user with id " + user.getId(), exception.getMessage());
    }

    @Test
    public void testGetCourseById_success() {
        Course mockCourse = new Course();
        mockCourse.setName("PE");
        mockCourse.setId("C01");

        when(courseRepository.findById("C01")).thenReturn(Optional.of(mockCourse));

        Course foundCourse = courseService.getCourseById("C01");

        assertNotNull(foundCourse);
        assertEquals("PE", foundCourse.getName());
    }

    @Test
    public void testGetCourseById_notFound() {
        when(courseRepository.findById("C01")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            courseService.getCourseById("C01");
        });

        assertEquals("Can't find course with id C01", exception.getMessage());
    }

    @Test
    public void testGetStudentsEnroll_whenStatusIsNotNull() {
        UserResponse mockUserResponse = new UserResponse();
        mockUserResponse.setName("John");
        course.setId(UUID.randomUUID().toString());

        List<Payment> mockPayments = Collections.singletonList(payment);
        List<User> mockUsers = mockPayments.stream().map(Payment::getUser).toList();
        List<UserResponse> mockResponses = List.of(mockUserResponse);

        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(paymentRepository.findByCourse(course)).thenReturn(mockPayments);
        when(modelMapper.map(mockUsers, new TypeToken<List<UserResponse>>() {}.getType())).thenReturn(mockResponses);

        List<UserResponse> result = courseService.getStudentsEnroll(UUID.fromString(course.getId()), null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getName());
    }

    @Test
    public void testGetStudentsEnroll_whenStatusIsNull() {
        UserResponse mockUserResponse = new UserResponse();
        mockUserResponse.setName("John");
        course.setId(UUID.randomUUID().toString());

        List<Payment> mockPayments = Collections.singletonList(payment);
        List<User> mockUsers = mockPayments.stream().map(Payment::getUser).toList();
        List<UserResponse> mockResponses = List.of(mockUserResponse);

        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(paymentRepository.findByCourseAndStatus(course,3)).thenReturn(mockPayments);
        when(modelMapper.map(mockUsers, new TypeToken<List<UserResponse>>() {}.getType())).thenReturn(mockResponses);

        List<UserResponse> result = courseService.getStudentsEnroll(UUID.fromString(course.getId()), PaymentStatus.SUCCESS);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.getFirst().getName());
    }

    @Test
    public void testGetStudentsEnroll_notFound() {
        UUID courseId = UUID.randomUUID();
        when(courseRepository.findById(courseId.toString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            courseService.getStudentsEnroll(courseId, null);
        });

        assertEquals("Can't find course with id " + courseId, exception.getMessage());
    }
}
