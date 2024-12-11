package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.entity.UserUsedVoucher;
import com.example.Mini_Project1.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserUsedVoucherRepository extends JpaRepository<UserUsedVoucher, String> {
    boolean existsByUserAndVoucher(User user, Voucher voucher);
}