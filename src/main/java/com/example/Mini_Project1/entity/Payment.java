package com.example.Mini_Project1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  // course
  @ManyToOne
  @JoinColumn(name = "course_id")
  private Course course;

  private float price;
  private float discount;

  // user
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  // voucher
  @ManyToOne
  @JoinColumn(name = "voucher_id")
  private Voucher voucher;

  private String content;
  private String paymentUrl;
  private int status; // 1: pending, 2: failed, 3: success
  private Date createdDate;
  private Date updatedDate;
}
