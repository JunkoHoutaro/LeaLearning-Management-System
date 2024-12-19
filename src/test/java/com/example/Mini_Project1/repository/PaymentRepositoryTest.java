package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Payment;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.entity.Voucher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    private User user;
    private Course course;
    private Voucher voucher;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user1");
        user.setName("Test User");
        userRepository.save(user);

        course = new Course();
        course.setId("course1");
        course.setName("Test Course");
        course.setPrice(1000);
        course.setDiscount(0.1f);
        courseRepository.save(course);

        voucher = new Voucher();
        voucher.setId("voucher1");
        voucher.setDiscountPercent(10);
        voucherRepository.save(voucher);
    }

    @Test
    void testExistsByUserAndCourse() {
        // Arrange
        Payment payment = new Payment();
        payment.setId("payment1");
        payment.setUser(user);
        payment.setCourse(course);
        payment.setPrice(900);
        payment.setDiscount(100);
        payment.setStatus(1);
        payment.setCreatedDate(new Date());
        payment.setUpdatedDate(new Date());
        paymentRepository.save(payment);

        // Act
        boolean exists = paymentRepository.existsByUserAndCourse(user, course);

        // Assert
        assertTrue(exists);
    }

    @Test
    void testFindByUserAndStatus() {
        // Arrange
        Payment payment = new Payment();
        payment.setId("payment1");
        payment.setUser(user);
        payment.setCourse(course);
        payment.setPrice(900);
        payment.setDiscount(100);
        payment.setStatus(1);
        payment.setCreatedDate(new Date());
        payment.setUpdatedDate(new Date());
        paymentRepository.save(payment);

        // Act
        List<Payment> payments = paymentRepository.findByUserAndStatus(user, 1);

        // Assert
        assertNotNull(payments);
        assertEquals(1, payments.size());
        assertEquals("payment1", payments.get(0).getId());
    }

    @Test
    void testFindByCourseAndStatus() {
        // Arrange
        Payment payment = new Payment();
        payment.setId("payment1");
        payment.setUser(user);
        payment.setCourse(course);
        payment.setPrice(900);
        payment.setDiscount(100);
        payment.setStatus(1);
        payment.setCreatedDate(new Date());
        payment.setUpdatedDate(new Date());
        paymentRepository.save(payment);

        // Act
        List<Payment> payments = paymentRepository.findByCourseAndStatus(course, 1);

        // Assert
        assertNotNull(payments);
        assertEquals(1, payments.size());
        assertEquals("payment1", payments.get(0).getId());
    }

    @Test
    void testExistsByVoucher() {
        // Arrange
        Payment payment = new Payment();
        payment.setId("payment1");
        payment.setUser(user);
        payment.setCourse(course);
        payment.setVoucher(voucher);
        payment.setPrice(900);
        payment.setDiscount(100);
        payment.setStatus(1);
        payment.setCreatedDate(new Date());
        payment.setUpdatedDate(new Date());
        paymentRepository.save(payment);

        // Act
        boolean exists = paymentRepository.existsByVoucher(voucher);

        // Assert
        assertTrue(exists);
    }
}