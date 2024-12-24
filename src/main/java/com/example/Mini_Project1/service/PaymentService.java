package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Cart;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Payment;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.entity.UserUsedVoucher;
import com.example.Mini_Project1.entity.Voucher;
import com.example.Mini_Project1.exception.CourseAlreadyPurchasedException;
import com.example.Mini_Project1.exception.CourseNotFoundException;
import com.example.Mini_Project1.exception.EmptyCartException;
import com.example.Mini_Project1.exception.NegativePriceException;
import com.example.Mini_Project1.exception.PaymentLinkCreationException;
import com.example.Mini_Project1.exception.PaymentNotFoundException;
import com.example.Mini_Project1.exception.UserNotFoundException;
import com.example.Mini_Project1.exception.VoucherAlreadyUsedException;
import com.example.Mini_Project1.repository.CartRepository;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.repository.PaymentRepository;
import com.example.Mini_Project1.repository.UserRepository;
import com.example.Mini_Project1.repository.UserUsedVoucherRepository;
import com.example.Mini_Project1.request.payment.CreatePaymentRequest;
import com.example.Mini_Project1.response.payment.PaymentResponse;
import com.example.Mini_Project1.response.payment.PaymentStatusResponse;
import com.example.Mini_Project1.response.voucher.VoucherResponse;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;

@Service
@AllArgsConstructor
public class PaymentService {

  private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
  private final CartRepository cartRepository;
  private final PaymentRepository paymentRepository;
  private final ModelMapper modelMapper;
  private final VoucherService voucherService;
  private final CourseRepository courseRepository;
  private final UserRepository userRepository;
  private final UserUsedVoucherRepository userUsedVoucherRepository;
  private final PayOS payOS;

  @Transactional
  public PaymentResponse createPayment(CreatePaymentRequest request) {
    if (request.getCourseId() == null || request.getUserId() == null) {
      throw new IllegalArgumentException("Course, User, or Content cannot be null");
    }

    Course course =
        courseRepository
            .findById(request.getCourseId())
            .orElseThrow(() -> new CourseNotFoundException("Course not found"));
    User user =
        userRepository
            .findById(request.getUserId())
            .orElseThrow(() -> new UserNotFoundException("User not found"));

    // Check if the user has already purchased the course
    if (paymentRepository.existsByUserAndCourse(user, course)) {
      throw new CourseAlreadyPurchasedException("User has already purchased this course");
    }

    Voucher voucher = null;
    if (request.getVoucherCode() != null) {
      VoucherResponse voucherResponse =
          voucherService.getVoucherByCodeService(request.getVoucherCode());
      voucher = modelMapper.map(voucherResponse, Voucher.class);

      // Check if the user has already used the voucher
      if (userUsedVoucherRepository.existsByUserAndVoucher(user, voucher)) {
        throw new VoucherAlreadyUsedException("User has already used this voucher");
      }
    }

    // Calculate the price after applying discounts
    float coursePrice = course.getPrice();
    float courseDiscount = course.getDiscount();
    float voucherDiscount = voucher != null ? voucher.getDiscountPercent() : 0;
    float priceAfterDiscount = coursePrice - (courseDiscount * coursePrice);
    float finalPrice = priceAfterDiscount - (voucherDiscount / 100 * priceAfterDiscount);

    if (finalPrice < 0) {
      throw new NegativePriceException("Final price cannot be negative");
    }

    String paymentUrl;
    Long orderCode;
    try {
      final String productName = course.getName();
      final String description = productName;
      final String returnUrl = "http://your-return-url.com";
      final String cancelUrl = "http://your-cancel-url.com";
      // Generate order code
      String currentTimeString = String.valueOf(new Date().getTime());
      orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));
      ItemData item =
          ItemData.builder().name(productName).quantity(1).price((int) finalPrice).build();
      PaymentData paymentData =
          PaymentData.builder()
              .orderCode(orderCode)
              .amount((int) finalPrice)
              .description(description)
              .returnUrl(returnUrl)
              .cancelUrl(cancelUrl)
              .item(item)
              .build();
      CheckoutResponseData data = payOS.createPaymentLink(paymentData);
      paymentUrl = data.getCheckoutUrl();
    } catch (Exception e) {
      logger.error("Failed to create payment link", e);
      throw new PaymentLinkCreationException("Failed to create payment link", e);
    }

    Payment newPayment =
        Payment.builder()
            .course(course)
            .user(user)
            .voucher(voucher)
            .price(finalPrice)
            .discount(courseDiscount)
            .content(orderCode.toString())
            .paymentUrl(paymentUrl)
            .status(1)
            .createdDate(new Date())
            .updatedDate(new Date())
            .build();

    Payment savedPayment = paymentRepository.save(newPayment);

    // Add a record to the UserUsedVoucher table
    if (voucher != null) {
      UserUsedVoucher userUsedVoucher =
          UserUsedVoucher.builder().user(user).voucher(voucher).build();
      userUsedVoucherRepository.save(userUsedVoucher);
    }

    return modelMapper.map(savedPayment, PaymentResponse.class);
  }

  @Transactional
  public List<PaymentResponse> checkoutCart(String userId, String voucherCode) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    Cart cart = cartRepository.findByUserId(userId);
    if (cart == null || cart.getCourseIds().isEmpty()) {
      throw new EmptyCartException("Cart is empty");
    }

    List<PaymentResponse> paymentResponses = new ArrayList<>();
    Voucher voucher = null;

    if (voucherCode != null) {
      VoucherResponse voucherResponse = voucherService.getVoucherByCodeService(voucherCode);
      voucher = modelMapper.map(voucherResponse, Voucher.class);
      if (voucher != null && userUsedVoucherRepository.existsByUserAndVoucher(user, voucher)) {
        throw new VoucherAlreadyUsedException("User has already used this voucher");
      }
    }

    float totalPrice = 0;
    for (String courseId : cart.getCourseIds()) {
      Course course =
          courseRepository
              .findById(courseId)
              .orElseThrow(
                  () -> new CourseNotFoundException("Course not found with id: " + courseId));
      float coursePrice = course.getPrice() - course.getDiscount() * course.getPrice();

      if (voucher != null) {
        float discount = coursePrice * voucher.getDiscountPercent() / 100;
        coursePrice -= discount;
      }

      if (coursePrice < 0) {
        throw new NegativePriceException("Final price cannot be negative");
      }

      totalPrice += coursePrice;
    }

    Long orderCode;
    String paymentUrl;

    try {
      String productName = "Courses Purchase";
      String description = "Multiple courses purchase";
      String returnUrl = "http://your-return-url.com";
      String cancelUrl = "http://your-cancel-url.com";
      String currentTimeString = String.valueOf(new Date().getTime());
      orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));
      ItemData item =
          ItemData.builder().name(productName).quantity(1).price((int) totalPrice).build();
      PaymentData paymentData =
          PaymentData.builder()
              .orderCode(orderCode)
              .amount((int) totalPrice)
              .description(description)
              .returnUrl(returnUrl)
              .cancelUrl(cancelUrl)
              .item(item)
              .build();
      CheckoutResponseData data = payOS.createPaymentLink(paymentData);
      paymentUrl = data.getCheckoutUrl();
    } catch (Exception e) {
      logger.error("Failed to create payment link", e);
      throw new PaymentLinkCreationException("Failed to create payment link", e);
    }

    for (String courseId : cart.getCourseIds()) {
      Course course =
          courseRepository
              .findById(courseId)
              .orElseThrow(
                  () -> new CourseNotFoundException("Course not found with id: " + courseId));
      float coursePrice = course.getPrice() - course.getDiscount() * course.getPrice();

      if (voucher != null) {
        float discount = coursePrice * voucher.getDiscountPercent() / 100;
        coursePrice -= discount;
      }

      Payment payment =
          Payment.builder()
              .user(user)
              .course(course)
              .price(coursePrice)
              .status(1)
              .paymentUrl(paymentUrl)
              .voucher(voucher)
              .content(orderCode.toString())
              .createdDate(new Date())
              .updatedDate(new Date())
              .build();

      paymentRepository.save(payment);

      PaymentResponse paymentResponse = modelMapper.map(payment, PaymentResponse.class);
      paymentResponses.add(paymentResponse);
    }

    // save voucher used
    if (voucher != null) {
      UserUsedVoucher userUsedVoucher =
          UserUsedVoucher.builder().user(user).voucher(voucher).build();
      userUsedVoucherRepository.save(userUsedVoucher);
    }

    cart.getCourseIds().clear();
    cartRepository.save(cart);

    return paymentResponses;
  }

  public PaymentStatusResponse checkPaymentStatus(String userId, String courseId) {
    userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
    Payment payment = paymentRepository.findByUserIdAndCourseId(userId, courseId);
    if (payment == null) {
      throw new PaymentNotFoundException("Payment not found");
    }

    Long orderCode = Long.parseLong(payment.getContent());
    PaymentLinkData paymentLinkData;
    try {
      paymentLinkData = payOS.getPaymentLinkInformation(orderCode);
    } catch (Exception e) {
      logger.error("Failed to get payment link information", e);
      throw new PaymentNotFoundException("Payment not found");
    }
    if (paymentLinkData == null) {
      throw new PaymentNotFoundException("Payment not found");
    }

    switch (paymentLinkData.getStatus().toLowerCase()) {
      case "cancelled" -> payment.setStatus(2);
      case "paid" -> payment.setStatus(3);
      case "pending" -> payment.setStatus(1);
      default -> payment.setStatus(0);
    }

    return new PaymentStatusResponse(paymentLinkData.getStatus(), paymentLinkData.getAmount());
  }
}
