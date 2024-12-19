package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.enums.Action;
import com.example.Mini_Project1.enums.CourseStatus;
import com.example.Mini_Project1.enums.PaymentStatus;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.request.course.CreateCourseRequest;
import com.example.Mini_Project1.request.course.UpdateCourseRequest;
import com.example.Mini_Project1.response.course.CourseResponse;
import com.example.Mini_Project1.response.user.UserResponse;
import com.example.Mini_Project1.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CourseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private List<CourseResponse> mockResponses;
    private CourseResponse response;
    private User user;
    private UpdateCourseRequest updateCourseRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID().toString());

        response = new CourseResponse();
        response.setId(UUID.randomUUID().toString());
        response.setName("Course 1");
        response.setStatus(1);
        response.setPrice(100f);
        response.setUserId(user.getId());

        CourseResponse response2 = new CourseResponse();
        response2.setName("Course 2");
        response2.setStatus(2);
        response2.setPrice(200f);
        response2.setUserId(user.getId());

        mockResponses = List.of(response, response2);

        updateCourseRequest = new UpdateCourseRequest();
        updateCourseRequest.setCourseId(UUID.fromString(response.getId()));
        updateCourseRequest.setName("Updated name");
    }

    @Test
    public void testCreateNewCourse_success() throws Exception {
        CreateCourseRequest createCourseRequest = new CreateCourseRequest(
                UUID.fromString("2cd97901-4ba5-4a45-8628-6b30108a6f35"),
                "Test course",
                100f,
                0.3f
        );

        CourseResponse response = new CourseResponse();
        response.setName("Test course");

        when(courseService.createCourse(any(CreateCourseRequest.class))).thenReturn(response);

        mockMvc.perform(post("/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCourseRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test course"));
    }

    @Test
    public void testCreateNewCourse_validationFailed() throws Exception {
        CreateCourseRequest createCourseRequest = new CreateCourseRequest(
                UUID.fromString("2cd97901-4ba5-4a45-8628-6b30108a6f35"),
                "",
                100f,
                0.3f
        );

        mockMvc.perform(post("/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCourseRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetCoursesByStatus_whenStatusIsNull() throws Exception {
        when(courseService.getCoursesByStatus(null)).thenReturn(mockResponses);

        mockMvc.perform(get("/course")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Course 1"))
                .andExpect(jsonPath("$[1].name").value("Course 2"));
    }

    @Test
    public void testGetCoursesByStatus_whenStatusIsNotNull() throws Exception {
        when(courseService.getCoursesByStatus(CourseStatus.ACCEPTED)).thenReturn(mockResponses);

        mockMvc.perform(get("/course")
                        .param("status", "ACCEPTED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Course 1"))
                .andExpect(jsonPath("$[1].name").value("Course 2"));
    }

    @Test
    public void testGetCoursesByName_withAllParams() throws Exception {
        when(courseService.searchCourses("course", CourseStatus.PENDING, true)).thenReturn(List.of(response));

        mockMvc.perform(get("/course/search")
                        .param("name", "course")
                        .param("courseStatus", "PENDING")
                        .param("priceAscending", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Course 1"));
    }

    @Test
    public void testGetCoursesByName_withoutCourseStatus() throws Exception {
        List<CourseResponse> reverseResponses = new ArrayList<> (mockResponses);
        Collections.reverse(reverseResponses);

        when(courseService.searchCourses("course", null, false)).thenReturn(reverseResponses);

        mockMvc.perform(get("/course/search")
                        .param("name", "course")
                        .param("priceAscending", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Course 2"))
                .andExpect(jsonPath("$[1].name").value("Course 1"));
    }

    @Test
    public void testGetCoursesByName_missingRequiredParam() throws Exception {
        mockMvc.perform(get("/course/search")
                        .param("priceAscending", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testGetCoursesByInstructor_whenCourseStatusIsNull() throws Exception {
        when(courseService.getCoursesByInstructor(UUID.fromString(user.getId()), null)).thenReturn(mockResponses);

        mockMvc.perform(get("/course/instructor")
                        .param("instructorId", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Course 1"))
                .andExpect(jsonPath("$[1].name").value("Course 2"));
    }

    @Test
    public void testGetCoursesByInstructor_whenCourseStatusIsNotNull() throws Exception {
        when(courseService.getCoursesByInstructor(UUID.fromString(user.getId()), CourseStatus.PENDING)).thenReturn(List.of(response));

        mockMvc.perform(get("/course/instructor")
                        .param("instructorId", user.getId())
                        .param("courseStatus", "PENDING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Course 1"));
    }

    @Test
    public void testGetCoursesByInstructor_notFound() throws Exception {
        when(courseService.getCoursesByInstructor(UUID.fromString(user.getId()), null))
                .thenThrow(new NotFoundException("Can't find user with id " + user.getId()));

        mockMvc.perform(get("/course/instructor")
                        .param("instructorId", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Can't find user with id " + user.getId()));
    }

    @Test
    public void testGetCoursesPurchased_whenPaymentStatusIsNull() throws Exception {
        when(courseService.getPurchasedCourses(UUID.fromString(user.getId()), null)).thenReturn(List.of(response));

        mockMvc.perform(get("/course/purchase")
                        .param("userId", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Course 1"));
    }

    @Test
    public void testGetCoursesPurchased_whenPaymentStatusIsNotNull() throws Exception {
        when(courseService.getPurchasedCourses(UUID.fromString(user.getId()), PaymentStatus.SUCCESS)).thenReturn(List.of(response));

        mockMvc.perform(get("/course/purchase")
                        .param("userId", user.getId())
                        .param("paymentStatus", "SUCCESS")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Course 1"));
    }

    @Test
    public void testGetCoursesPurchased_notFound() throws Exception {
        when(courseService.getPurchasedCourses(UUID.fromString(user.getId()), null)).thenThrow(
                new NotFoundException("Can't find user with id " + user.getId())
        );

        mockMvc.perform(get("/course/purchase")
                        .param("userId", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Can't find user with id " + user.getId()));
    }

    @Test
    public void testActionOnCourse_success() throws Exception {
        when(courseService.actionOnCourse(UUID.fromString(response.getId()), Action.ACCEPT)).thenReturn(response);

        mockMvc.perform(get("/course/action")
                        .param("courseId", response.getId())
                        .param("action", "ACCEPT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Course 1"))
                .andExpect(jsonPath("$.status").value(1));
    }

    @Test
    public void testActionOnCourse_missingRequiredParam() throws Exception {
        mockMvc.perform(get("/course/action")
                        .param("courseId", response.getId())
                        .param("action", "INVALID_ACTION")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testActionOnCourse_notFound() throws Exception {
        when(courseService.actionOnCourse(UUID.fromString(response.getId()), Action.ACCEPT))
                .thenThrow(new NotFoundException("Can't find course with id " + response.getId()));

        mockMvc.perform(get("/course/action")
                        .param("courseId", response.getId())
                        .param("action", "ACCEPT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Can't find course with id " + response.getId()));
    }

    @Test
    public void testGetStudentsEnroll_whenPaymentStatusIsNull() throws Exception {
        UserResponse studentResponse1 = new UserResponse();
        UserResponse studentResponse2 = new UserResponse();

        List<UserResponse> students = List.of(studentResponse1, studentResponse2);
        when(courseService.getStudentsEnroll(UUID.fromString(response.getId()), null)).thenReturn(students);

        mockMvc.perform(get("/course/student")
                        .param("courseId", response.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testGetStudentsEnroll_whenPaymentStatusIsNotNull() throws Exception{
        UserResponse studentResponse = new UserResponse();

        when(courseService.getStudentsEnroll(UUID.fromString(response.getId()), PaymentStatus.SUCCESS)).thenReturn(List.of(studentResponse));

        mockMvc.perform(get("/course/student")
                        .param("courseId", response.getId())
                        .param("paymentStatus", "SUCCESS")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void testGetStudentsEnroll_notFound() throws Exception {
        when(courseService.getStudentsEnroll(UUID.fromString(response.getId()), null)).thenThrow(
                new NotFoundException("Can't find course with id " + response.getId())
        );

        mockMvc.perform(get("/course/student")
                        .param("courseId", response.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Can't find course with id " + response.getId()));
    }

    @Test
    public void testUpdateCourse_success() throws Exception {
        response.setName(updateCourseRequest.getName());

        when(courseService.updateCourse(any(UpdateCourseRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated name"));
    }

    @Test
    public void testUpdateCourse_badRequest() throws Exception {
        mockMvc.perform(patch("/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateCourseRequest())))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateCourse_notFound() throws Exception {
        when(courseService.updateCourse(any(UpdateCourseRequest.class)))
                .thenThrow(new NotFoundException("Can't find course with id "+ response.getId()));

        mockMvc.perform(patch("/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Can't find course with id "+ response.getId()));
    }

    @Test
    public void testDeleteCourse_success() throws Exception {
        response.setStatus(3);

        when(courseService.deleteCourse(UUID.fromString(response.getId()))).thenReturn(response);

        mockMvc.perform(delete("/course")
                        .param("courseId", response.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.status").value(3));
    }

    @Test
    public void testDeleteCourse_notFound() throws Exception {
        when(courseService.deleteCourse(UUID.fromString(response.getId())))
                .thenThrow(new NotFoundException("Can't find course with id " +response.getId()));

        mockMvc.perform(delete("/course")
                        .param("courseId", response.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Can't find course with id " +response.getId()));
    }
}
