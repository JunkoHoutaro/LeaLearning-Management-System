package com.example.Mini_Project1.config;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.Mini_Project1.exception.ErrorResponse;
import com.example.Mini_Project1.service.CustomUserDetailsService;
import com.example.Mini_Project1.utils.JwtTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;
    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = extractJwtFromRequest(request);

            if (token != null && jwtTokenUtils.validateToken(token)) {
                String userId = jwtTokenUtils.getUserIdFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
                UsernamePasswordAuthenticationToken authentication
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            } else {
                logger.error("Invalid or missing JWT token"); // Add error logging
                handleAuthenticationError(response, HttpStatus.FORBIDDEN, "Invalid or missing JWT token");
            }
        } catch (Exception e) {
            logger.error("JWT Authentication failed: " + e.getMessage()); // Add error logging
            handleAuthenticationError(response, HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    private void handleAuthenticationError(HttpServletResponse response, HttpStatus status, String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                "Access denied",
                "Access Denied: " + message
        );

        try {
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            logger.error("Authentication error: {}" + jsonResponse); // Add error logging
            response.getWriter().write(jsonResponse);
            response.getWriter().flush(); // Ensure the response is flushed
        } catch (Exception e) {
            logger.error("Error writing JSON response: ", e); // Log any JSON processing errors
            // Fallback plain text response if JSON fails
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("Authentication error: " + message);
            response.getWriter().flush();
        }
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs/");
    }
}
