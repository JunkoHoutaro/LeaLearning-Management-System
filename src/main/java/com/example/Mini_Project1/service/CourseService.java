package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Payment;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.enums.Action;
import com.example.Mini_Project1.enums.CourseStatus;
import com.example.Mini_Project1.enums.PaymentStatus;
import com.example.Mini_Project1.exception.BadRequestException;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.repository.PaymentRepository;
import com.example.Mini_Project1.repository.UserRepository;
import com.example.Mini_Project1.request.course.CreateCourseRequest;
import com.example.Mini_Project1.request.course.UpdateCourseRequest;
import com.example.Mini_Project1.response.course.CourseResponse;
import com.example.Mini_Project1.response.user.UserResponse;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request) {
        User user = userRepository.findById(request.getUserId().toString()).orElseThrow
                (()-> new NotFoundException("Can't find user with id " + request.getUserId().toString()));

        if(courseRepository.existsByNameAndUser(request.getName(), user))
            throw new BadRequestException("This instructor has created a course with the same name");

        Course course = modelMapper.map(request, Course.class);
        course.setUser(user);
        course.setCreatedDate(new Date());
        course.setUpdatedDate(new Date());
        course.setStatus(1);

        return modelMapper.map(courseRepository.save(course), CourseResponse.class);
    }

    public List<CourseResponse> getCoursesByStatus(CourseStatus status) {
        List<Course> courses = status == null? courseRepository.findAll() : courseRepository.findCourseByStatus(status.getValue());
        return modelMapper.map(courses, new TypeToken<List<CourseResponse>>() {}.getType());
    }

    public List<CourseResponse> searchCourses(String name, CourseStatus courseStatus, boolean priceAscending) {
        List<Course> courses = courseStatus == null?
                courseRepository.findCourseByNameContainingIgnoreCase(name) :
                courseRepository.findCourseByNameContainingIgnoreCaseAndStatus(name,courseStatus.getValue());

        // Sort result
        if(priceAscending) courses.sort(Comparator.comparing(Course::getPrice));
        else courses.sort(Comparator.comparing(Course::getPrice).reversed());

        return modelMapper.map(courses, new TypeToken<List<CourseResponse>>() {
        }.getType());
    }

    public List<CourseResponse> getPurchasedCourses(UUID userId, PaymentStatus status) {
        User user = userRepository.findById(userId.toString()).orElseThrow(
                ()-> new NotFoundException("Can't find user with id " + userId));

        List<Payment> payments = status == null ? paymentRepository.findByUser(user) : paymentRepository.findByUserAndStatus(user, status.getValue());
        List<Course> courses = payments.stream().map(Payment::getCourse).toList();

        return modelMapper.map(courses, new TypeToken<List<CourseResponse>>() {
        }.getType());
    }

    public List<CourseResponse> getCoursesByInstructor(UUID instructorId, CourseStatus status) {
        User user = userRepository.findById(instructorId.toString()).orElseThrow(
                ()-> new NotFoundException("Can't find user with id " + instructorId));

        List<Course> courses = status == null? courseRepository.findCourseByUser(user) : courseRepository.findCourseByUserAndStatus(user, status.getValue());
        return modelMapper.map(courses, new TypeToken<List<CourseResponse>>() {}.getType());
    }

    @Transactional
    public CourseResponse updateCourse(UpdateCourseRequest request) {
        Course course = courseRepository.findById(request.getCourseId().toString()).orElseThrow(
                ()-> new NotFoundException("Can't find user with id " + request.getCourseId().toString()));

        // Load instructor
        Hibernate.initialize(course.getUser());
        if(request.getName() != null && courseRepository.existsByNameAndUser(request.getName(),course.getUser()))
            throw new BadRequestException("This instructor has created a course with the same name");

        course.setUpdatedDate(new Date());
        modelMapper.map(request, course);

        return modelMapper.map(courseRepository.save(course), CourseResponse.class);
    }

    public CourseResponse deleteCourse(UUID courseId) {
        Course course = courseRepository.findById(courseId.toString()).orElseThrow(
                ()-> new NotFoundException("Can't find user with id " + courseId));

        // Delete course -> change status to delete(3)
        course.setStatus(3);
        course.setUpdatedDate(new Date());

        return modelMapper.map(courseRepository.save(course), CourseResponse.class);
    }

    public CourseResponse actionOnCourse(UUID courseId, Action action) {
        Course course = courseRepository.findById(courseId.toString()).orElseThrow(
                () -> new NotFoundException("Can't find course with id " + courseId));

        if (course.getStatus() != 1)
            throw new BadRequestException("The course status is not 'Pending'");

        if (action.equals(Action.ACCEPT))
            course.setStatus(2);
        if (action.equals(Action.DECLINE))
            course.setStatus(3);

        return modelMapper.map(courseRepository.save(course), CourseResponse.class);
    }

    public List<UserResponse> getStudentsEnroll(UUID courseId, PaymentStatus status) {
        Course course = courseRepository.findById(courseId.toString()).orElseThrow(
                () -> new NotFoundException("Can't find course with id " + courseId)
        );

        List<Payment> payments = status == null? paymentRepository.findByCourse(course) : paymentRepository.findByCourseAndStatus(course, status.getValue());
        List<User> users = payments.stream().map(Payment::getUser).toList();
        return modelMapper.map(users, new TypeToken<List<UserResponse>>() {}.getType());
    }
    public Course getCourseById(String courseId) {
        return courseRepository.findById(courseId.toString()).orElseThrow(
                () -> new NotFoundException("Can't find course with id " + toString()));
    }
}
