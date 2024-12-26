package com.example.Mini_Project1.request.chapter;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UpdateChapterRequest {

    @NotNull(message = "Chapter id is required")
    private UUID chapterId;

    @Positive(message = "Chapter index must be greater than 0.")
    private Integer index;

    private String name;
}
