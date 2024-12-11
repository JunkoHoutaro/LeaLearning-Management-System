package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.response.cart.CartResponse;
import com.example.Mini_Project1.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@AllArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addCourseToCart(@RequestParam String userId, @RequestParam String courseId) {
        return ResponseEntity.ok(cartService.addCourseToCart(userId, courseId));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<CartResponse> removeCourseFromCart(@RequestParam String userId, @RequestParam String courseId) {
        return ResponseEntity.ok(cartService.removeCourseFromCart(userId, courseId));
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable String cartId) {
        return ResponseEntity.ok(cartService.getCart(cartId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CartResponse> getCartByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }
}