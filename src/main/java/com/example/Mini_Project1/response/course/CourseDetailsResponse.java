package com.example.Mini_Project1.response.course;

import com.example.Mini_Project1.response.chapter.ChapterDetailsResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class CourseDetailsResponse {
    private String userId;
    private CourseResponse course;
    private List<ChapterDetailsResponse> chapters;
}
