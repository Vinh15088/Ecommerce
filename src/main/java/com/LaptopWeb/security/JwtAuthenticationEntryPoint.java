package com.LaptopWeb.security;

import com.LaptopWeb.dto.response.ApiResponse;
import com.LaptopWeb.exception.ErrorApp;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    // call this method when authentication failure
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {
        ErrorApp errorApp = ErrorApp.UNAUTHENTICATED;

        response.setStatus(errorApp.getHttpStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .success(true)
                .content(errorApp.getMessage())
                .build();

        // ApiResponse -> JSON
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

        response.flushBuffer();
    }
}
