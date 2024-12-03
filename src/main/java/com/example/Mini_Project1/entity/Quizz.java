package com.example.Mini_Project1.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quizz")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quizz {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private float duration;
  private String name;
  private Date createdDate;
  private Date updatedDate;

  // course
  @OneToOne
  @JoinColumn(name = "course_id")
  private Course course;

  // chapter
  @OneToOne
  @JoinColumn(name = "chapter_id")
  private Chapter chapter;

  // question
  @OneToMany(mappedBy = "quizz")
  private List<Question> questions;

  // score
  @OneToMany(mappedBy = "quizz")
  private List<Score> scores;
}
