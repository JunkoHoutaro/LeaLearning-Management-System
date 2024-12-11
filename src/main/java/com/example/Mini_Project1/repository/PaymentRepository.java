package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Payment;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.entity.Voucher;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
  @EntityGraph(attributePaths = {"course"})
  List<Payment> findByUserAndStatus(User user, int status);
  boolean existsByVoucher(Voucher voucher);
}
