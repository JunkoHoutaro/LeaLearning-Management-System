package com.example.Mini_Project1.response.voucher;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public class VoucherResponse implements Serializable{
    private String id;
    private float discountPercent;
    private String name;
    private String code;
}
