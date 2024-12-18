package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.Payment;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.entity.Voucher;
import com.example.Mini_Project1.response.AdminDashboard.TopCourseResponseRegistration;
import com.example.Mini_Project1.response.AdminDashboard.TopCourseResponseRevenue;
import com.example.Mini_Project1.response.AdminDashboard.TopInstructorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .build());

        course = courseRepository.save(Course.builder()
                .name("Test Course")
                .build());
    }

    @Test
    void testFindByUserAndStatus() {
        Payment payment = Payment.builder()
                .user(user)
                .course(course)
                .status(1)
                .build();
        paymentRepository.save(payment);

        List<Payment> payments = paymentRepository.findByUserAndStatus(user, 1);
        assertFalse(payments.isEmpty());
        assertEquals(1, payments.get(0).getStatus());
    }

    @Test
    void testFindTopCoursesByRevenue() {
        Payment payment = Payment.builder()
                .user(user)
                .course(course)
                .price(100)
                .discount(10)
                .status(3)
                .build();
        paymentRepository.save(payment);

        List<TopCourseResponseRevenue> topCourses = paymentRepository.findTopCoursesByRevenue();
        assertFalse(topCourses.isEmpty());
        assertEquals(course.getId(), topCourses.get(0).getCourseId());
    }

    @Test
    void testFindTopCoursesByRegistration() {
        Payment payment = Payment.builder()
                .user(user)
                .course(course)
                .status(3)
                .build();
        paymentRepository.save(payment);

        List<TopCourseResponseRegistration> topCourses = paymentRepository.findTopCoursesByRegistration();
        assertFalse(topCourses.isEmpty());
        assertEquals(course.getId(), topCourses.get(0).getCourseId());
    }

    @Test
    void testFindTopInstructorsByRegistration() {
        user.setRole("instructor");
        userRepository.save(user);

        Payment payment = Payment.builder()
                .user(user)
                .course(course)
                .status(3)
                .build();
        paymentRepository.save(payment);

        List<TopInstructorResponse> topInstructors = paymentRepository.findTopInstructorsByRegistration();
        assertFalse(topInstructors.isEmpty());
        assertEquals(user.getId(), topInstructors.get(0).getId());
    }

    @Test
    void testFindByCourseAndStatus() {
        Payment payment = Payment.builder()
                .user(user)
                .course(course)
                .status(1)
                .build();
        paymentRepository.save(payment);

        List<Payment> payments = paymentRepository.findByCourseAndStatus(course, 1);
        assertFalse(payments.isEmpty());
        assertEquals(1, payments.get(0).getStatus());
    }

    @Test
    void testFindByUser() {
        Payment payment = Payment.builder()
                .user(user)
                .course(course)
                .build();
        paymentRepository.save(payment);

        List<Payment> payments = paymentRepository.findByUser(user);
        assertFalse(payments.isEmpty());
        assertEquals(user.getId(), payments.get(0).getUser().getId());
    }

    @Test
    void testFindByCourse() {
        Payment payment = Payment.builder()
                .user(user)
                .course(course)
                .build();
        paymentRepository.save(payment);

        List<Payment> payments = paymentRepository.findByCourse(course);
        assertFalse(payments.isEmpty());
        assertEquals(course.getId(), payments.get(0).getCourse().getId());
    }

    @Test
    void testExistsByVoucher() {
        Voucher voucher = Voucher.builder()
                .code("DISCOUNT10")
                .build();
        voucherRepository.save(voucher);

        Payment payment = Payment.builder()
                .user(user)
                .course(course)
                .voucher(voucher)
                .build();
        paymentRepository.save(payment);

        boolean exists = paymentRepository.existsByVoucher(voucher);
        assertTrue(exists);
    }
}