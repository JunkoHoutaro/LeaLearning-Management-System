package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoucherRepository extends JpaRepository<Voucher, String> {
    boolean existsByCode(String code);
    List<Voucher> findByName(String name);
    List<Voucher> findByCode(String code);

}
