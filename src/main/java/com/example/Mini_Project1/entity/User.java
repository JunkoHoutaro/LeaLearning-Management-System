package com.example.Mini_Project1.entity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String email;
    private LocalDate dob;
    private String password;
    private String role;
    private Date createdDate;
    private Date updatedDate;

    // course
    @OneToMany(mappedBy = "user")
    private List<Course> courses;

    // score
    @OneToMany(mappedBy = "user")
    private List<Score> scores;

    // comment
    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    // rating
    @OneToMany(mappedBy = "user")
    private List<Rating> ratings;

    // user used voucher
    @OneToMany(mappedBy = "user")
    private List<UserUsedVoucher> userUsedVouchers;

    // payment
    @OneToMany(mappedBy = "user")
    private List<Payment> payments;
}
