package com.example.Mini_Project1.response.chapter;

import com.example.Mini_Project1.response.lesson.LessonResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ChapterDetailsResponse {
    private ChapterResponse chapter;
    private List<LessonResponse> lessons;
}
