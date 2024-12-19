package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.response.cart.CartResponse;
import com.example.Mini_Project1.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private CartResponse sampleCartResponse;

    @BeforeEach
    void setUp() {
        sampleCartResponse = new CartResponse();
        sampleCartResponse.setId("cart123");
        sampleCartResponse.setUserId("user123");
        sampleCartResponse.setCourseIds(Arrays.asList("course1", "course2"));
        sampleCartResponse.setTotalPrice(199.98f);
        sampleCartResponse.setTotalAmount(2);
    }

    @Test
    void addCourseToCart_ShouldReturnCartResponse() {
        // Arrange
        String userId = "user123";
        String courseId = "course1";
        when(cartService.addCourseToCart(userId, courseId)).thenReturn(sampleCartResponse);

        // Act
        ResponseEntity<CartResponse> response = cartController.addCourseToCart(userId, courseId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(sampleCartResponse, response.getBody());
        verify(cartService, times(1)).addCourseToCart(userId, courseId);
    }

    @Test
    void removeCourseFromCart_ShouldReturnCartResponse() {
        // Arrange
        String userId = "user123";
        String courseId = "course1";
        when(cartService.removeCourseFromCart(userId, courseId)).thenReturn(sampleCartResponse);

        // Act
        ResponseEntity<CartResponse> response = cartController.removeCourseFromCart(userId, courseId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(sampleCartResponse, response.getBody());
        verify(cartService, times(1)).removeCourseFromCart(userId, courseId);
    }

    @Test
    void getCart_ShouldReturnCartResponse() {
        // Arrange
        String cartId = "cart123";
        when(cartService.getCart(cartId)).thenReturn(sampleCartResponse);

        // Act
        ResponseEntity<CartResponse> response = cartController.getCart(cartId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(sampleCartResponse, response.getBody());
        verify(cartService, times(1)).getCart(cartId);
    }

    @Test
    void getCartByUserId_ShouldReturnCartResponse() {
        // Arrange
        String userId = "user123";
        when(cartService.getCartByUserId(userId)).thenReturn(sampleCartResponse);

        // Act
        ResponseEntity<CartResponse> response = cartController.getCartByUserId(userId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(sampleCartResponse, response.getBody());
        verify(cartService, times(1)).getCartByUserId(userId);
    }
}