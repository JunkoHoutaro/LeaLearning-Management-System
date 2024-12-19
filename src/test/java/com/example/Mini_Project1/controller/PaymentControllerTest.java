package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.request.payment.CreatePaymentRequest;
import com.example.Mini_Project1.response.payment.PaymentResponse;
import com.example.Mini_Project1.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(PaymentController.class)
@Import(PaymentControllerTest.SecurityConfig.class) // Import mock security config
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock security configuration to disable CSRF
    static class SecurityConfig {
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            return http.csrf().disable().build();
        }
    }

    @Test
    @WithMockUser // Mock authentication
    public void testCreatePayment() throws Exception {
        // Arrange
        CreatePaymentRequest createPaymentRequest = new CreatePaymentRequest();
        createPaymentRequest.setUserId("user123");
        createPaymentRequest.setCourseId("course456");

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setId("payment789");
        paymentResponse.setUserId("user123");
        paymentResponse.setCourseId("course456");
        paymentResponse.setPrice(100.0f);
        paymentResponse.setDiscount(10.0f);
        paymentResponse.setStatus(1);

        Mockito.when(paymentService.createPayment(any(CreatePaymentRequest.class))).thenReturn(paymentResponse);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPaymentRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("payment789"))
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.courseId").value("course456"))
                .andExpect(jsonPath("$.price").value(100.0f))
                .andExpect(jsonPath("$.discount").value(10.0f))
                .andExpect(jsonPath("$.status").value(1));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER") // Specify the roles for the mock user
    public void testCheckoutCart() throws Exception {
        // Arrange
        String userId = "user123";
        String voucherCode = "voucher789";

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setId("payment789");
        paymentResponse.setUserId(userId);
        paymentResponse.setCourseId("course456");
        paymentResponse.setPrice(90.0f);
        paymentResponse.setDiscount(10.0f);
        paymentResponse.setStatus(1);

        Mockito.when(paymentService.checkoutCart(eq(userId), eq(voucherCode))).thenReturn(paymentResponse);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/payments/checkout")
                        .param("userId", userId)
                        .param("voucherCode", voucherCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("payment789"))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.price").value(90.0f))
                .andExpect(jsonPath("$.discount").value(10.0f))
                .andExpect(jsonPath("$.status").value(1));
    }
}
