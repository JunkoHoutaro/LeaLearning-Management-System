package com.example.Mini_Project1.entity;

import java.util.Date;

import org.apache.tomcat.jni.Library;

import jakarta.annotation.Generated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import jakarta.persistence.Entity;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lesson")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String name;
  private String resource_url;
  private String video_url;
  private Date createdDate;
  private Date updatedDate;
  private int isDemo;

  // course
  @ManyToOne
  @JoinColumn(name = "chapter_id")
  private Chapter chapter;
}
