package com.example.Mini_Project1.request.voucher;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class UpdateVoucherRequest {
    @NotNull(message = "Voucher id is required")
    private UUID voucherId;

    @Positive(message = "Discount percent must be greater than 0")
    @Range(max = 99)
    private Float discountPercent;

    private String name;
    private String code;
}
