package com.example.Mini_Project1.response.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class CourseResponse implements Serializable {
  private String id;

  private String name;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createdDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date updatedDate;

  private float price;

  private float discount;

  private int status;

  private String userId;
}
