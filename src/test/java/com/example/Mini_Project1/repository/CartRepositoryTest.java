package com.example.Mini_Project1.repository;

import com.example.Mini_Project1.entity.Cart;
import com.example.Mini_Project1.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        // Set up a User to associate with the Cart
        user = userRepository.save(User.builder()
                .id("user123")
                .name("John Doe")
                .email("john.doe@example.com")
                .build());    }

    @Test
    void testFindByUserId() {
        // Create a Cart associated with the user
        Cart cart = Cart.builder().user(user).courseIds(java.util.List.of("course1", "course2")).build();
        cartRepository.save(cart);

        // Test that the cart is correctly retrieved by userId
        Cart foundCart = cartRepository.findByUserId(user.getId());

        assertNotNull(foundCart);
        assertEquals(user.getId(), foundCart.getUser().getId());
        assertTrue(foundCart.getCourseIds().contains("course1"));
        assertTrue(foundCart.getCourseIds().contains("course2"));
    }

    @Test
    void testFindByUserId_WhenNoCartExists() {
        // Test that no cart is found if the user does not have a cart
        Cart foundCart = cartRepository.findByUserId(user.getId());
        assertNull(foundCart);  // No cart should be found
    }

    @Test
    void testSaveCart() {
        // Create and save a Cart
        Cart cart = Cart.builder().user(user).courseIds(java.util.List.of("course3")).build();
        Cart savedCart = cartRepository.save(cart);

        // Test that the cart was saved and has an ID
        assertNotNull(savedCart.getId());
        assertEquals(user.getId(), savedCart.getUser().getId());
        assertTrue(savedCart.getCourseIds().contains("course3"));
    }

    @Test
    void testDeleteCart() {
        // Create and save a Cart
        Cart cart = Cart.builder().user(user).courseIds(java.util.List.of("course4")).build();
        Cart savedCart = cartRepository.save(cart);

        // Delete the Cart
        cartRepository.delete(savedCart);

        // Test that the Cart was deleted
        Optional<Cart> deletedCart = cartRepository.findById(savedCart.getId());
        assertFalse(deletedCart.isPresent());  // The cart should no longer exist
    }

    @Test
    void testFindById() {
        // Create and save a Cart
        Cart cart = Cart.builder().user(user).courseIds(java.util.List.of("course5")).build();
        Cart savedCart = cartRepository.save(cart);

        // Test that the Cart is found by its ID
        Optional<Cart> foundCart = cartRepository.findById(savedCart.getId());
        assertTrue(foundCart.isPresent());
        assertEquals(savedCart.getId(), foundCart.get().getId());
    }

    @Test
    void testCartNotFoundByInvalidId() {
        // Test that no Cart is found by an invalid ID
        Optional<Cart> foundCart = cartRepository.findById("invalid-id");
        assertFalse(foundCart.isPresent());  // Cart should not be found
    }
}
