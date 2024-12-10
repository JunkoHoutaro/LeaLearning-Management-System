package com.example.Mini_Project1.response.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class FileResponse {
    private String result;
    private Integer size;
    private String url;
}
