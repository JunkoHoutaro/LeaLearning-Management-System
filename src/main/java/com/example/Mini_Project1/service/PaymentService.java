package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Payment;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.entity.Voucher;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.repository.PaymentRepository;
import com.example.Mini_Project1.repository.UserRepository;
import com.example.Mini_Project1.request.payment.CreatePaymentRequest;
import com.example.Mini_Project1.response.payment.PaymentResponse;
import com.example.Mini_Project1.response.voucher.VoucherResponse;
import com.example.Mini_Project1.config.ModelMapperConfig;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import com.example.Mini_Project1.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Date;

@Service
@AllArgsConstructor
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    private final VoucherService voucherService;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PayOS payOS;

    public PaymentResponse createPayment(CreatePaymentRequest request) {
        if (request.getCourseId() == null || request.getUserId() == null) {
            throw new IllegalArgumentException("Course, User, or Content cannot be null");
        }

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new NotFoundException("Course not found"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        VoucherResponse voucherResponse = voucherService.getVoucherByCodeService(request.getVoucherCode());
        Voucher voucher = modelMapper.map(voucherResponse, Voucher.class);

        // Calculate the price after applying discounts
        float coursePrice = course.getPrice();
        float courseDiscount = course.getDiscount();
        float voucherDiscount = voucher != null ? voucher.getDiscountPercent() : 0;
        float priceAfterDiscount = coursePrice - (courseDiscount * coursePrice);
        float finalPrice = priceAfterDiscount - (voucherDiscount / 100 * priceAfterDiscount);

        if (finalPrice < 0) {
            throw new RuntimeException("Final price cannot be negative");
        }

        String paymentUrl;
        Long orderCode;
        try {
            final String productName = course.getName();
            final String description = "Payment for " + productName;
            final String returnUrl = "http://your-return-url.com";
            final String cancelUrl = "http://your-cancel-url.com";
            // Generate order code
            String currentTimeString = String.valueOf(new Date().getTime());
            orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));
            ItemData item = ItemData.builder().name(productName).quantity(1).price((int) finalPrice).build();
            PaymentData paymentData = PaymentData.builder().orderCode(orderCode).amount((int) finalPrice).description(description)
                    .returnUrl(returnUrl).cancelUrl(cancelUrl).item(item).build();
            CheckoutResponseData data = payOS.createPaymentLink(paymentData);
            paymentUrl = data.getCheckoutUrl();
        } catch (Exception e) {
            logger.error("Failed to create payment link", e);
            throw new RuntimeException("Failed to create payment link", e);
        }

        Payment newPayment = Payment.builder()
                .course(course)
                .user(user)
                .voucher(voucher)
                .price(finalPrice)
                .discount(courseDiscount + voucherDiscount)
                .content(orderCode.toString())
                .paymentUrl(paymentUrl)
                .status(1)
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();

        Payment savedPayment = paymentRepository.save(newPayment);
        return modelMapper.map(savedPayment, PaymentResponse.class);
    }
}