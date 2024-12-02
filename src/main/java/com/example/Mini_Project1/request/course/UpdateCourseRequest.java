package com.example.Mini_Project1.request.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class UpdateCourseRequest {
  @NotNull(message = "Course id is required")
  private UUID courseId;

  private String name;

  @PositiveOrZero(message = "Price must be greater than or equal to 0")
  private Float price;

  @Range(min = 0, max = 1)
  private Float discount;

  @Range(min = 1, max = 2, message = "Course status must be 1(PENDING) or 2(ACCEPT)")
  private Integer status;
}
