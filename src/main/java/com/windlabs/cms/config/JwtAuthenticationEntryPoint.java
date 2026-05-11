package com.windlabs.cms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.windlabs.cms.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("path", request.getRequestURI());
        error.put("method", request.getMethod());
        error.put("detail", authException.getMessage());

        ApiResponse<Object> apiResponse = ApiResponse.error(
                HttpStatus.UNAUTHORIZED.value(),
                "Authentication required",
                error
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}