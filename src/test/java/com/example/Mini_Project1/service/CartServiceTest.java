package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Cart;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.repository.CartRepository;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.repository.UserRepository;
import com.example.Mini_Project1.response.cart.CartResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Course testCourse;
    private Cart testCart;
    private CartResponse testCartResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user123");

        testCourse = new Course();
        testCourse.setId("course123");
        testCourse.setPrice(99.99f);

        testCart = new Cart();
        testCart.setId("cart123");
        testCart.setUser(testUser);
        testCart.setCourseIds(new ArrayList<>());

        testCartResponse = new CartResponse();
        testCartResponse.setId("cart123");
        testCartResponse.setUserId("user123");
        testCartResponse.setCourseIds(new ArrayList<>());
        testCartResponse.setTotalPrice(0);
        testCartResponse.setTotalAmount(0);
    }

    @Test
    void addCourseToCart_NewCart_Success() {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(courseRepository.findById("course123")).thenReturn(Optional.of(testCourse));
        when(cartRepository.findByUserId("user123")).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        when(modelMapper.map(any(Cart.class), eq(CartResponse.class))).thenReturn(testCartResponse);

        // Act
        CartResponse response = cartService.addCourseToCart("user123", "course123");

        // Assert
        assertNotNull(response);
        verify(cartRepository).save(any(Cart.class));
        verify(userRepository).findById("user123");
        verify(courseRepository).findById("course123");
    }

    @Test
    void addCourseToCart_ExistingCart_Success() {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(courseRepository.findById("course123")).thenReturn(Optional.of(testCourse));
        when(cartRepository.findByUserId("user123")).thenReturn(testCart);
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        when(modelMapper.map(any(Cart.class), eq(CartResponse.class))).thenReturn(testCartResponse);

        // Act
        CartResponse response = cartService.addCourseToCart("user123", "course123");

        // Assert
        assertNotNull(response);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addCourseToCart_CourseAlreadyInCart_ThrowsException() {
        // Arrange
        testCart.setCourseIds(new ArrayList<>(Arrays.asList("course123")));
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(courseRepository.findById("course123")).thenReturn(Optional.of(testCourse));
        when(cartRepository.findByUserId("user123")).thenReturn(testCart);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> cartService.addCourseToCart("user123", "course123"),
                "Course already in cart"
        );
    }

    @Test
    void removeCourseFromCart_Success() {
        // Arrange
        testCart.setCourseIds(new ArrayList<>(Arrays.asList("course123")));
        when(cartRepository.findByUserId("user123")).thenReturn(testCart);
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        when(modelMapper.map(any(Cart.class), eq(CartResponse.class))).thenReturn(testCartResponse);

        // Act
        CartResponse response = cartService.removeCourseFromCart("user123", "course123");

        // Assert
        assertNotNull(response);
        verify(cartRepository).save(any(Cart.class));
        verify(cartRepository).findByUserId("user123");
        assertTrue(testCart.getCourseIds().isEmpty());
    }

    @Test
    void removeCourseFromCart_CartNotFound_ThrowsException() {
        // Arrange
        when(cartRepository.findByUserId("user123")).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> cartService.removeCourseFromCart("user123", "course123"),
                "Cart not found"
        );
    }

    @Test
    void removeCourseFromCart_CourseNotInCart_ThrowsException() {
        // Arrange
        when(cartRepository.findByUserId("user123")).thenReturn(testCart);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> cartService.removeCourseFromCart("user123", "course123"),
                "Course not in cart"
        );
    }

    @Test
    void getCart_Success() {
        // Arrange
        testCart.setCourseIds(Arrays.asList("course123"));
        when(cartRepository.findById("cart123")).thenReturn(Optional.of(testCart));
        when(courseRepository.findById("course123")).thenReturn(Optional.of(testCourse));
        when(modelMapper.map(any(Cart.class), eq(CartResponse.class))).thenReturn(testCartResponse);

        // Act
        CartResponse response = cartService.getCart("cart123");

        // Assert
        assertNotNull(response);
        assertEquals("cart123", response.getId());
    }

    @Test
    void getCart_NotFound_ThrowsException() {
        // Arrange
        when(cartRepository.findById("cart123")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> cartService.getCart("cart123"),
                "Cart not found"
        );
    }

    @Test
    void getCartByUserId_Success() {
        // Arrange
        testCart.setCourseIds(Arrays.asList("course123"));
        when(cartRepository.findByUserId("user123")).thenReturn(testCart);
        when(courseRepository.findById("course123")).thenReturn(Optional.of(testCourse));
        when(modelMapper.map(any(Cart.class), eq(CartResponse.class))).thenReturn(testCartResponse);

        // Act
        CartResponse response = cartService.getCartByUserId("user123");

        // Assert
        assertNotNull(response);
        verify(cartRepository).findByUserId("user123");
    }
}