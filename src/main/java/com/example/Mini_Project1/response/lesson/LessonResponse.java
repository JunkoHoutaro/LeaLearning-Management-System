package com.example.Mini_Project1.response.lesson;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class LessonResponse implements Serializable {

    private String id;

    private String name;

    private Integer index;

    private String resourceUrl;

    private String videoUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedDate;

    private int isDemo;

    private String chapterId;
}
