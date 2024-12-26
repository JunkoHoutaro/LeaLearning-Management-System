package com.example.Mini_Project1.config;

import com.example.Mini_Project1.exception.ErrorResponse;
import com.example.Mini_Project1.service.CustomUserDetailsService;
import com.example.Mini_Project1.utils.JwtTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

  private final JwtTokenUtils jwtTokenUtils;
  private final CustomUserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String token = extractJwtFromRequest(request);
      if (token != null && jwtTokenUtils.validateToken(token)) {
        String userId = jwtTokenUtils.getUserIdFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
      } else {
        ErrorResponse errorResponse =
            new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Access denied",
                "Access Denied: No valid JWT token found");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), errorResponse);
      }
    } catch (Exception e) {
      ErrorResponse errorResponse =
          new ErrorResponse(
              HttpStatus.FORBIDDEN.value(), "Access denied", "Access Denied: " + e.getMessage());
      response.setStatus(HttpStatus.FORBIDDEN.value());
      response.setContentType("application/json");
      new ObjectMapper().writeValue(response.getWriter(), errorResponse);
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
    return path.startsWith("/swagger-ui")
        || path.startsWith("/v3/api-docs")
        || path.startsWith("/auth");
  }
}
