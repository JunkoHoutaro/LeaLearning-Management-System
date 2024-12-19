package com.example.Mini_Project1.response.rating;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingResponse {
    private String id;
    private float rating;
    private String feedback;
    private String courseName;
    private String userName;
    private String userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;
}