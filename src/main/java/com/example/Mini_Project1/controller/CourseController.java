package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.service.CourseService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CourseController {
    private final CourseService courseService;
}
