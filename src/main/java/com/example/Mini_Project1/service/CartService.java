package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Cart;
import com.example.Mini_Project1.entity.Course;
import com.example.Mini_Project1.entity.User;
import com.example.Mini_Project1.exception.CartNotFoundException;
import com.example.Mini_Project1.exception.CourseAlreadyInCartException;
import com.example.Mini_Project1.exception.CourseAlreadyPurchasedException;
import com.example.Mini_Project1.exception.CourseNotFoundException;
import com.example.Mini_Project1.exception.CourseNotInCartException;
import com.example.Mini_Project1.exception.UserNotFoundException;
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
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    Course course =
        courseRepository
            .findById(courseId)
            .orElseThrow(() -> new CourseNotFoundException("Course not found"));

    // Check if the user has already purchased the course
    if (paymentRepository.existsByUserAndCourse(user, course)) {
      throw new CourseAlreadyPurchasedException("User has already purchased this course");
    }

    Cart cart = cartRepository.findByUserId(userId);
    if (cart == null) {
      cart = Cart.builder().user(user).courseIds(new ArrayList<>()).build();
    }

    if (cart.getCourseIds().contains(courseId)) {
      throw new CourseAlreadyInCartException("Course already in cart");
    }

    cart.getCourseIds().add(courseId);
    return mapCartToCartResponse(cartRepository.save(cart));
  }

  @Transactional
  public CartResponse removeCourseFromCart(String userId, String courseId) {
    Cart cart = cartRepository.findByUserId(userId);
    if (cart == null) {
      throw new CartNotFoundException("Cart not found for user id: " + userId);
    }

    if (!cart.getCourseIds().contains(courseId)) {
      throw new CourseNotInCartException("Course not in cart");
    }

    cart.getCourseIds().remove(courseId);
    return mapCartToCartResponse(cartRepository.save(cart));
  }

  public CartResponse getCart(String cartId) {
    Cart cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(() -> new CartNotFoundException("Cart not found with id: " + cartId));
    return mapCartToCartResponse(cart);
  }

  public CartResponse getCartByUserId(String userId) {
    Cart cart = cartRepository.findByUserId(userId);
    if (cart == null) {
      throw new CartNotFoundException("Cart not found for user id: " + userId);
    }
    return mapCartToCartResponse(cart);
  }

  private CartResponse mapCartToCartResponse(Cart cart) {
    CartResponse cartResponse = modelMapper.map(cart, CartResponse.class);
    float totalPrice = 0;
    for (String courseId : cart.getCourseIds()) {
      Course course =
          courseRepository
              .findById(courseId)
              .orElseThrow(
                  () -> new CourseNotFoundException("Course not found with id: " + courseId));
      totalPrice += course.getPrice();
    }
    cartResponse.setTotalPrice(totalPrice);
    cartResponse.setTotalAmount(cart.getCourseIds().size());
    return cartResponse;
  }
}
