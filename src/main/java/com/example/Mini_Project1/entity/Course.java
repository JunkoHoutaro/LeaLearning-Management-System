package com.example.Mini_Project1.entity;

import java.util.Date;
import java.util.List;

import org.apache.tomcat.jni.Library;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "course")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private Date createdDate;
    private Date updatedDate;
    private float price;
    private float discount;
    private int status;

    // user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // course
    @OneToMany(mappedBy = "course")
    private List<Chapter> chapters;

    // comment
    @OneToMany(mappedBy = "course")
    private List<Comment> books;

    // rating
    @OneToMany(mappedBy = "course")
    private List<Rating> ratings;

    // payment
    @OneToMany(mappedBy = "course")
    private List<Payment> payments;
}
