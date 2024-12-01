package com.example.Mini_Project1.request.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class CreateCourseRequest {
    @NotNull(message = "User id is required")
    private UUID userId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be greater than or equal to 0")
    private float price;

    @Range(min = 0, max = 1)
    private float discount;
}
