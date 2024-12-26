package com.example.Mini_Project1.response.QuizzAndQuestion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public class QuestionResponse implements Serializable {
    private String id;
    private Integer index;
    private String options;
    private char correct;
    private String content;
    private String quizzId;
}
