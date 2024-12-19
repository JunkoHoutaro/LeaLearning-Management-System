package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Cart;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.repository.CartRepository;
import com.example.Mini_Project1.repository.CourseRepository;
import com.example.Mini_Project1.repository.PaymentRepository;
import com.example.Mini_Project1.repository.UserRepository;
import com.example.Mini_Project1.response.cart.CartResponse;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CartService {
  private final CartRepository cartRepository;
  private final UserRepository userRepository;
  private final CourseRepository courseRepository;
  private final PaymentRepository paymentRepository;
  private final ModelMapper modelMapper;

  @Transactional
  public CartResponse addCourseToCart(String userId, String courseId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new RuntimeException("Course not found"));

    // Check if the user has already purchased the course
    if (paymentRepository.existsByUserAndCourse(user, course)) {
      throw new RuntimeException("User has already purchased this course");
    }

    Cart cart = cartRepository.findByUserId(userId);
    if (cart == null) {
      cart = Cart.builder().user(user).courseIds(new ArrayList<>()).build();
    }

    if (cart.getCourseIds().contains(courseId)) {
      throw new RuntimeException("Course already in cart");
    }

    cart.getCourseIds().add(courseId);
    return mapCartToCartResponse(cartRepository.save(cart));
  }

  @Transactional
  public CartResponse removeCourseFromCart(String userId, String courseId) {
    Cart cart = cartRepository.findByUserId(userId);
    if (cart == null) {
      throw new RuntimeException("Cart not found");
    }

    if (!cart.getCourseIds().contains(courseId)) {
      throw new RuntimeException("Course not in cart");
    }

    cart.getCourseIds().remove(courseId);
    return mapCartToCartResponse(cartRepository.save(cart));
  }

  public CartResponse getCart(String cartId) {
    Cart cart =
        cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Cart not found"));
    return mapCartToCartResponse(cart);
  }

  public CartResponse getCartByUserId(String userId) {
    Cart cart = cartRepository.findByUserId(userId);
    return mapCartToCartResponse(cart);
  }

  private CartResponse mapCartToCartResponse(Cart cart) {
    CartResponse cartResponse = modelMapper.map(cart, CartResponse.class);
    float totalPrice = 0;
    for (String courseId : cart.getCourseIds()) {
      Course course =
          courseRepository
              .findById(courseId)
              .orElseThrow(() -> new RuntimeException("Course not found"));
      totalPrice += course.getPrice();
    }
    cartResponse.setTotalPrice(totalPrice);
    cartResponse.setTotalAmount(cart.getCourseIds().size());
    return cartResponse;
  }
}
