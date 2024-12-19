package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.*;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.repository.*;
import com.example.Mini_Project1.request.payment.CreatePaymentRequest;
import com.example.Mini_Project1.response.payment.PaymentResponse;
import com.example.Mini_Project1.response.voucher.VoucherResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentData;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserUsedVoucherRepository userUsedVoucherRepository;

    @Mock
    private VoucherService voucherService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PayOS payOS;

    private Course course;
    private User user;
    private Voucher voucher;
    private Cart cart;
    private CreatePaymentRequest createPaymentRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        course = new Course();
        course.setId("course1");
        course.setPrice(1000);
        course.setDiscount(0.1f);

        user = new User();
        user.setId("user1");

        voucher = new Voucher();
        voucher.setId("voucher1");
        voucher.setDiscountPercent(10);

        cart = new Cart();
        cart.setUser(user);
        cart.setCourseIds(List.of("course1"));

        createPaymentRequest = new CreatePaymentRequest();
        createPaymentRequest.setCourseId("course1");
        createPaymentRequest.setUserId("user1");
        createPaymentRequest.setVoucherCode("voucher1");
    }

    @Test
    void createPayment_shouldReturnPaymentResponse_whenPaymentIsCreatedSuccessfully() throws Exception {
        // Arrange
        when(courseRepository.findById("course1")).thenReturn(Optional.of(course));
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));

        // Manually create a VoucherResponse and set properties
        VoucherResponse voucherResponse = new VoucherResponse();
        voucherResponse.setId("voucher1");
        voucherResponse.setDiscountPercent(10);

        when(voucherService.getVoucherByCodeService("voucher1")).thenReturn(voucherResponse);

        PaymentData paymentData = mock(PaymentData.class);
        CheckoutResponseData checkoutResponseData = mock(CheckoutResponseData.class);
        when(payOS.createPaymentLink(paymentData)).thenReturn(checkoutResponseData);
        when(checkoutResponseData.getCheckoutUrl()).thenReturn("http://payment-url.com");

        Payment savedPayment = new Payment();
        savedPayment.setId("payment1");
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // Act
        PaymentResponse paymentResponse = paymentService.createPayment(createPaymentRequest);

        // Assert
        assertNotNull(paymentResponse);
        assertEquals("payment1", paymentResponse.getId());
        assertEquals("http://payment-url.com", paymentResponse.getPaymentUrl());
    }

    @Test
    void createPayment_shouldThrowNotFoundException_whenCourseNotFound() {
        // Arrange
        when(courseRepository.findById("course1")).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> paymentService.createPayment(createPaymentRequest));
        assertEquals("Course not found", exception.getMessage());
    }

    @Test
    void createPayment_shouldThrowNotFoundException_whenUserNotFound() {
        // Arrange
        when(courseRepository.findById("course1")).thenReturn(Optional.of(course));
        when(userRepository.findById("user1")).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> paymentService.createPayment(createPaymentRequest));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void checkoutCart_shouldReturnPaymentResponse_whenPaymentIsSuccessful() throws Exception {
        // Arrange
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId("user1")).thenReturn(cart);
        when(courseRepository.findById("course1")).thenReturn(Optional.of(course));
        when(voucherService.getVoucherByCodeService("voucher1")).thenReturn(new VoucherResponse());

        PaymentData paymentData = mock(PaymentData.class);
        CheckoutResponseData checkoutResponseData = mock(CheckoutResponseData.class);
        when(payOS.createPaymentLink(paymentData)).thenReturn(checkoutResponseData);
        when(checkoutResponseData.getCheckoutUrl()).thenReturn("http://payment-url.com");

        Payment savedPayment = new Payment();
        savedPayment.setId("payment1");
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // Act
        PaymentResponse paymentResponse = paymentService.checkoutCart("user1", "voucher1");

        // Assert
        assertNotNull(paymentResponse);
        assertEquals("payment1", paymentResponse.getId());
        assertEquals("http://payment-url.com", paymentResponse.getPaymentUrl());
    }

    @Test
    void checkoutCart_shouldThrowRuntimeException_whenCartIsEmpty() {
        // Arrange
        cart.setCourseIds(List.of()); // Empty cart
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId("user1")).thenReturn(cart);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> paymentService.checkoutCart("user1", null));
        assertEquals("Cart is empty", exception.getMessage());
    }

    @Test
    void checkoutCart_shouldThrowRuntimeException_whenCourseNotFound() {
        // Arrange
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId("user1")).thenReturn(cart);
        when(courseRepository.findById("course1")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> paymentService.checkoutCart("user1", null));
        assertEquals("Course not found", exception.getMessage());
    }
}
