package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Payment;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.repository.PaymentRepository;
import com.example.Mini_Project1.repository.UserRepository;
import com.example.Mini_Project1.request.course.CreateCourseRequest;
import com.example.Mini_Project1.request.course.UpdateCourseRequest;
import com.example.Mini_Project1.response.course.CourseResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request) {
        User user = userRepository.findById(request.getUserId().toString()).orElseThrow
                (()-> new RuntimeException("Can't find user with id " + request.getUserId().toString()));

        if(courseRepository.existsByNameAndUser(request.getName(), user))
            throw new RuntimeException("This instructor has created a course with the same name");

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        Course course = modelMapper.map(request, Course.class);
        course.setUser(user);
        course.setCreatedDate(new Date());
        course.setUpdatedDate(new Date());
        course.setStatus(1);

        return modelMapper.map(courseRepository.save(course), CourseResponse.class);
    }

    public List<CourseResponse> getCoursesByStatus(Integer status) {
        if(status == null)
        {
            List<Course> courses = courseRepository.findAll();
            return new ModelMapper().map(courses, new TypeToken<List<CourseResponse>>() {}.getType());
        }

        List<Course> courses = courseRepository.findCourseByStatus(status);
        return new ModelMapper().map(courses, new TypeToken<List<CourseResponse>>() {}.getType());
    }

    public List<CourseResponse> searchCourses(String name, boolean priceAscending) {
        // Exclude deleted course
        List<Course> courses = courseRepository.findCourseByNameContainingIgnoreCaseAndStatusNot(name,3);

        // Sort result
        if(priceAscending)
            courses.sort(Comparator.comparing(Course::getPrice));
        else
            courses.sort(Comparator.comparing(Course::getPrice).reversed());

        return new ModelMapper().map(courses, new TypeToken<List<CourseResponse>>() {}.getType());
    }

    public List<CourseResponse> getPurchasedCourses(UUID userId) {
        User user = userRepository.findById(userId.toString()).orElseThrow(
                ()-> new RuntimeException("Can't find user with id " + userId.toString()));

        List<Payment> payments = paymentRepository.findByUserAndStatus(user,3);
        List<Course> courses = payments.stream().map(Payment::getCourse).toList();

        return new ModelMapper().map(courses, new TypeToken<List<CourseResponse>>() {}.getType());
    }

    public List<CourseResponse> getCoursesByInstructor(UUID instructorId) {
        User user = userRepository.findById(instructorId.toString()).orElseThrow(
                ()-> new RuntimeException("Can't find user with id " + instructorId.toString()));

        List<Course> courses = courseRepository.findCourseByUser(user);

        return new ModelMapper().map(courses, new TypeToken<List<CourseResponse>>() {}.getType());
    }

    @Transactional
    public CourseResponse updateCourse(UpdateCourseRequest request) {
        Course course = courseRepository.findById(request.getCourseId().toString()).orElseThrow(
                ()-> new RuntimeException("Can't find user with id " + request.getCourseId().toString()));

        // Load instructor
        Hibernate.initialize(course.getUser());
        if(request.getName() != null && courseRepository.existsByNameAndUser(request.getName(),course.getUser()))
            throw new RuntimeException("This instructor has created a course with the same name");

        course.setUpdatedDate(new Date());

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(request, course);

        return modelMapper.map(courseRepository.save(course), CourseResponse.class);
    }

    public CourseResponse deleteCourse(UUID courseId) {
        Course course = courseRepository.findById(courseId.toString()).orElseThrow(
                ()-> new RuntimeException("Can't find user with id " + courseId.toString()));

        // Delete course -> change status to delete(3)
        course.setStatus(3);
        course.setUpdatedDate(new Date());

        return new ModelMapper().map(courseRepository.save(course), CourseResponse.class);
    }
}
