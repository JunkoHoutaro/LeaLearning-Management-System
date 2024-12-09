package com.example.Mini_Project1.request.chapter;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UpdateChapterRequest {

    @NotNull(message = "Chapter id is required")
    private UUID chapterId;

    @NotBlank(message = "Chapter name is required")
    private String name;

}
