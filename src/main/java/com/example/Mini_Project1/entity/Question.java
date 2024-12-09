package com.example.Mini_Project1.entity;

import lombok.*;
import org.apache.tomcat.jni.Library;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "question")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String options;
  private char correct;
  private String content;

  // quizz
  @ManyToOne
  @JoinColumn(name = "quizz_id")
  private Quizz quizz;
}
