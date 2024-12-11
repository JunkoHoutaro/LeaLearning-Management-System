package com.example.Mini_Project1.request.voucher;

import com.example.Mini_Project1.entity.UserUsedVoucher;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@AllArgsConstructor
@Getter
public class CreateVoucherRequest {
    @NotNull(message = "Discount percent of voucher must be provided")
    @Positive(message = "Discount percent must be greater than 0")
    @Range(max = 99)
    private float discountPercent;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Voucher code must be provided")
    private String code;
}
